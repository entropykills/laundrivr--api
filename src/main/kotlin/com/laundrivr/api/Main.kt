package com.laundrivr.api

import com.laundrivr.api.environment.LaundrivrApiEnvironment
import com.laundrivr.api.service.ApiService
import com.laundrivr.api.service.payment.PaymentApiService
import com.laundrivr.api.service.RootApiService
import com.laundrivr.api.service.v1.V1CheckUpdateApiService
import com.squareup.square.Environment
import com.squareup.square.SquareClient
import io.github.cdimascio.dotenv.dotenv
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.javalin.Javalin
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    // require '-t' (for target environment) of production or development
    if (args.size != 2 || args[0] != "-t") {
        println("Usage: -t <target environment>")
        return
    }

    // require target environment to be production or development
    if (args[1] != "production" && args[1] != "development") {
        println("Target environment must be production or development")
        return
    }

    // set the target environment
    val targetEnvironment = args[1]
    val dotenv = dotenv {
        filename = ".env.${targetEnvironment}"
        ignoreIfMalformed = false
        ignoreIfMissing = false
    }

    // initialize api environment
    val apiEnvironment = LaundrivrApiEnvironment(dotenv)

    LaundrivrApiService(apiEnvironment)
}

class LaundrivrApiService(private val apiEnvironment: LaundrivrApiEnvironment) {

    private val squareClient: SquareClient = SquareClient.Builder()
        .environment(Environment.valueOf(apiEnvironment.squareEnvironment.uppercase()))
        .accessToken(apiEnvironment.squareAccessToken)
        .build()
    private val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = apiEnvironment.supabaseUrl,
        supabaseKey = apiEnvironment.supabaseServiceRoleKey
    ) {
        install(Functions)
        install(Postgrest)
        install(GoTrue)
    }

    init {
        runBlocking {
            supabaseClient.gotrue.importAuthToken(apiEnvironment.supabaseServiceRoleKey)
        }

        start()
    }

    private fun start() {
        val app = Javalin.create().apply {
            exception(Exception::class.java) { e, ctx ->
                e.printStackTrace()
                ctx.result(e.message ?: "Unknown error").status(500)
            }
            error(404) { ctx -> ctx.json("not found") }
        }.start(apiEnvironment.port)

        val services: List<ApiService> = listOf(
            RootApiService(),
            PaymentApiService(),
            V1CheckUpdateApiService()
        )

        services.forEach { it.register(app, squareClient, supabaseClient, apiEnvironment) }
    }
}