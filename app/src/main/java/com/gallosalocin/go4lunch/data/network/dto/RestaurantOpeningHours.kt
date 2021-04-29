package com.gallosalocin.go4lunch.data.network.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RestaurantOpeningHours(
        @SerializedName("open_now")
        var isOpenNow: Boolean = false,
) : Serializable {

    override fun toString(): String {
        return "RestaurantOpeningHours{" +
                "openNow=" + isOpenNow +
                '}'
    }
}