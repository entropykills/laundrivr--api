package service

import com.beust.klaxon.Parser
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestResult
import io.javalin.http.Context
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PaymentApiService() : ApiService() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun routes(): List<Route> {
        return listOf(Route("/payment/webhook", HttpMethod.POST) { ctx -> handler(ctx) })
    }

    data class PaymentWebhookPayload(
        @JsonProperty("data") val data: PaymentWebhookData
    )

    data class PaymentWebhookData(
        @JsonProperty("object") val `object`: PaymentWebhookObject
    )

    data class PaymentWebhookObject(
        @JsonProperty("payment") val payment: PaymentWebhookPayment
    )

    data class PaymentWebhookPayment(
        @JsonProperty("order_id") val orderId: String
    )

    @Serializable
    data class OrderResult(
        @SerialName("square_order_id") val orderId: String,
        @SerialName("original_square_customer_id") val customerId: String,
        @SerialName("user_id") val userId: String,
        @SerialName("created_at") val createdAt: String,
    )

    private fun handler(ctx: Context) {
        var orderId: String? = null

        try {
            val payload: com.beust.klaxon.JsonObject = Parser.default().parse(ctx.bodyInputStream()) as com.beust.klaxon.JsonObject
            payload["data"]?.let { data ->
                data as com.beust.klaxon.JsonObject
                data["object"]?.let { obj ->
                    obj as com.beust.klaxon.JsonObject
                    obj["payment"]?.let {
                        it as com.beust.klaxon.JsonObject
                        orderId = it["order_id"] as String
                    }
                }
            }
        } catch (e: Exception) {
            println("Error parsing payload: $e")
            ctx.result("Payload is invalid.").status(500)
            return
        }

        val order = this.squareClient?.ordersApi?.retrieveOrder(orderId) ?: throw Exception("Order not found.")
        val packageId = order.order?.lineItems?.get(0)?.catalogObjectId ?: throw Exception("Package not found.")

        val orderResult: PostgrestResult? = runBlocking {
            supabaseClient?.postgrest?.from("pending_transactions")?.select {
                OrderResult::orderId eq orderId
            }
        }

        if (orderResult == null) {
            ctx.result("Order not found in pending transactions.").status(500)
            return
        }

        val orderResultObject = try {
            orderResult.decodeSingle<OrderResult>()
        } catch (e: Exception) {
            ctx.result("Error decoding order result.").status(500)
            return
        }

        val userId = orderResultObject.userId

        @Serializable
        data class FunctionBody(
            @SerialName("user_id") val userId: String, @SerialName("package_id") val packageId: String
        )

        val functionResult = runBlocking {
            supabaseClient?.functions(function = "square-payment-callback",
                body = FunctionBody(userId, packageId),
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "application/json")
                })
        }

        if ((functionResult == null) || (functionResult.status != HttpStatusCode.OK)) {
            ctx.result("Error calling function.").status(500)
            return
        }
    }
}