package com.gallosalocin.go4lunch.services.dto

import com.gallosalocin.go4lunch.models.RestaurantResult
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RestaurantResponse(
        @SerializedName("html_attributions")
        var htmlAttributions: List<Any>? = null,

        @SerializedName("next_page_token")
        var nextPageToken: String? = null,

        @SerializedName("results")
        var restaurantResults: List<RestaurantResult>? = null,
        var status: String? = null,
) : Serializable {

    override fun toString(): String {
        return "RestaurantResponse{" +
                "htmlAttributions=" + htmlAttributions +
                ", nextPageToken='" + nextPageToken + '\'' +
                ", restaurantResults=" + restaurantResults +
                ", status='" + status + '\'' +
                '}'
    }

//    constructor() {}
//    constructor(htmlAttributions: List<Any>?, nextPageToken: String?, restaurantResults: List<RestaurantResult>?, status: String?) {
//        this.htmlAttributions = htmlAttributions
//        this.nextPageToken = nextPageToken
//        this.restaurantResults = restaurantResults
//        this.status = status
//    }

}