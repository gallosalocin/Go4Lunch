package com.gallosalocin.go4lunch.services.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RestaurantPhoto(
        var height: Int = 0,

        @SerializedName("photo_reference")
        var photoReference: String? = null,
        var width: Int = 0,
) : Serializable {

//    constructor() {}
//    constructor(height: Int, photoReference: String?, width: Int) {
//        this.height = height
//        this.photoReference = photoReference
//        this.width = width
//    }

    override fun toString(): String {
        return "Photo{" +
                "height=" + height +
                ", photo_reference='" + photoReference + '\'' +
                ", width=" + width +
                '}'
    }
}