package com.gallosalocin.go4lunch.data.network.dto

import java.io.Serializable

data class RestaurantGeometry(
        var location: RestaurantLocation,
) : Serializable {

    override fun toString(): String {
        return "RestaurantGeometry{" +
                "location=" + location +
                '}'
    }
}