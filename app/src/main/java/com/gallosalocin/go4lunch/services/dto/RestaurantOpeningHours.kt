package com.gallosalocin.go4lunch.services.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RestaurantOpeningHours(
        @SerializedName("open_now")
        var isOpenNow: Boolean = false,
) : Serializable {

//    constructor(openNow: Boolean) {
//        isOpenNow = openNow
//    }
//
//    constructor() {}

    override fun toString(): String {
        return "RestaurantOpeningHours{" +
                "openNow=" + isOpenNow +
                '}'
    }
}