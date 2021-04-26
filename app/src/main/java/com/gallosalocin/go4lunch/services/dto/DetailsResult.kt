package com.gallosalocin.go4lunch.services.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailsResult(
        @SerializedName("formatted_phone_number")
        var formattedPhoneNumber: String?,

        @SerializedName("opening_hours")
        var restaurantOpeningHours: RestaurantOpeningHours?,

        @SerializedName("geometry")
        var detailsGeometry: RestaurantGeometry,
        var name: String,

        @SerializedName("photos")
        var detailsPhotos: List<RestaurantPhoto>?,
        var rating: Float = 0f,

        @SerializedName("vicinity")
        var address: String,
        var website: String?,
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