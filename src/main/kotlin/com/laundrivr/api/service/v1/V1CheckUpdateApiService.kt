package com.laundrivr.api.service.v1;

import com.laundrivr.api.Constants
import com.laundrivr.api.model.Platform
import com.laundrivr.api.service.ApiService
import io.javalin.http.Context
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class V1CheckUpdateApiService() : ApiService() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun routes(): List<Route> {
        return listOf(Route("/v1/check-update", HttpMethod.GET) { ctx -> handler(ctx) })
    }

    @Serializable
    data class CheckUpdateResponse(
        @SerialName("supported_versions_on_this_platform") val supportedVersionsOnThisPlatform: List<String>,
        @SerialName("update_url") val updateUrl: String,
    )

    private fun handler(ctx: Context) {
        // get the query parameters
        // get the enum platform from the string
        val platformEnum = when (ctx.queryParam("platform") ?: "ios") { // default to ios
            Platform.IOS.value -> Platform.IOS
            Platform.ANDROID.value -> Platform.ANDROID
            else -> Platform.IOS
        }

        // get a list of supported app versions
        val supportedAppVersions = Constants.SUPPORTED_APP_VERSIONS;
        // return all the app versions that match the platform
        val appVersions = supportedAppVersions.filter { it.platform == platformEnum }
        // map the app versions into just the version string
        val versions = appVersions.map { it.version }

        val updateUrl: String = when (platformEnum) {
            Platform.IOS -> Constants.IOS_UPDATE_URL
            Platform.ANDROID -> Constants.ANDROID_UPDATE_URL
        }

        // construct the response
        val response = CheckUpdateResponse(
            supportedVersionsOnThisPlatform = versions,
            updateUrl = updateUrl
        )

        // return the response, serialized as json
        ctx.json(response)
    }
}