package com.gallosalocin.go4lunch.models

import com.gallosalocin.go4lunch.services.dto.RestaurantGeometry
import com.gallosalocin.go4lunch.services.dto.RestaurantOpeningHours
import com.gallosalocin.go4lunch.services.dto.RestaurantPhoto
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RestaurantResult(
        var geometry: RestaurantGeometry,
        var name: String,
        @SerializedName("opening_hours")
        var restaurantOpeningHours: RestaurantOpeningHours?,
        @SerializedName("vicinity")
        var address: String,
        @SerializedName("place_id")
        var placeId: String,
        @SerializedName("photos")
        var restaurantPhotos: List<RestaurantPhoto>?,
        var rating: Float = 0f,
        var distance: Int = 0,
        var workmates: Float = 0F,
) : Serializable {

    override fun toString(): String {
        return "RestaurantResult{" +
                "name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", rating=" + rating +
                '}'
    }

}