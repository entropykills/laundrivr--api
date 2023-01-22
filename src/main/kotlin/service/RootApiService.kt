package service

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