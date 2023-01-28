package com.laundrivr.api.model

sealed class Platform(val value: String) {
    object IOS : Platform("ios")
    object ANDROID : Platform("android")
}