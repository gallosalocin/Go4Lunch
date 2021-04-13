package com.gallosalocin.go4lunch.services.dto

import java.io.Serializable

data class RestaurantGeometry (
        var location: RestaurantLocation? = null,
): Serializable {

//    constructor() {}
//    constructor(location: RestaurantLocation?) {
//        this.location = location
//    }

    override fun toString(): String {
        return "RestaurantGeometry{" +
                "location=" + location +
                '}'
    }
}