package com.laundrivr.api

import com.laundrivr.api.model.AppVersion
import com.laundrivr.api.model.Platform

class Constants {

    companion object {
        val SUPPORTED_APP_VERSIONS = listOf(
            AppVersion("1.0.5+2", Platform.IOS),
            AppVersion("1.0.5+2", Platform.ANDROID),
            AppVersion("1.0.5+00 [UNUSED]", Platform.ANDROID)
        )

        val ANDROID_UPDATE_URL = "https://play.google.com/store/apps/details?id=com.laundrivr.laundrivr"
        val IOS_UPDATE_URL = "https://apps.apple.com/us/app/laundrivr/id6444505198/"
    }
}
