package com.gallosalocin.go4lunch.data.network.dto

import java.io.Serializable

data class RestaurantLocation(
        var lat: Float = 0f,
        var lng: Float = 0f,
) : Serializable {

    override fun toString(): String {
        return "RestaurantLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}'
    }
}