package com.laundrivr.api.service

import com.squareup.square.SquareClient
import io.github.jan.supabase.SupabaseClient
import io.javalin.Javalin
import io.javalin.http.Context

abstract class ApiService() {

    var squareClient: SquareClient? = null
    var supabaseClient: SupabaseClient? = null

    enum class HttpMethod {
        GET, POST, PUT, DELETE
    }

    class Route(val path: String, val method: HttpMethod, val handler: (Context) -> Unit)


    fun register(app: Javalin, squareClient: SquareClient, supabaseClient: SupabaseClient) {
        this.squareClient = squareClient
        this.supabaseClient = supabaseClient
        routes().forEach { route ->
            when (route.method) {
                HttpMethod.GET -> app.get(route.path, route.handler)
                HttpMethod.POST -> app.post(route.path, route.handler)
                HttpMethod.PUT -> app.put(route.path, route.handler)
                HttpMethod.DELETE -> app.delete(route.path, route.handler)
            }
        }
    }

    abstract fun routes(): List<Route>
}