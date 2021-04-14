package com.gallosalocin.go4lunch.services.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailsResult(
        @SerializedName("formatted_phone_number")
        var formattedPhoneNumber: String? = null,

        @SerializedName("opening_hours")
        var restaurantOpeningHours: RestaurantOpeningHours? = null,

        @SerializedName("geometry")
        var detailsGeometry: RestaurantGeometry? = null,
        var name: String? = null,

        @SerializedName("photos")
        var detailsPhotos: List<RestaurantPhoto>? = null,
        var rating: Float = 0f,

        @SerializedName("vicinity")
        var address: String? = null,
        var website: String? = null,
) : Serializable {

    override fun toString(): String {
        return "DetailsResult{" +
                "formattedPhoneNumber='" + formattedPhoneNumber + '\'' +
                ", detailsGeometry=" + detailsGeometry +
                ", name='" + name + '\'' +
                ", detailsPhotos=" + detailsPhotos +
                ", rating=" + rating +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                '}'
    }

}