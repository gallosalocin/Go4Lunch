package com.gallosalocin.go4lunch.services.dto

import java.io.Serializable

data class RestaurantLocation(
        var lat: Float = 0f,
        var lng: Float = 0f,
) : Serializable {

//    constructor() {}
//    constructor(lat: Float, lng: Float) {
//        this.lat = lat
//        this.lng = lng
//    }

    override fun toString(): String {
        return "RestaurantLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}'
    }
}