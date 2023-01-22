package com.laundrivr.api.service

import com.laundrivr.api.service.ApiService

class RootApiService(): ApiService() {

    override fun routes(): List<Route> {
        return listOf(
            Route(
                path = "/",
                method = HttpMethod.GET,
                handler = { ctx ->
                    ctx.result("access denied").status(403)
                }
            )
        )
    }
}