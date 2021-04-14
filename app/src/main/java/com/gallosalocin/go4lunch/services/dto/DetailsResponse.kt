package com.gallosalocin.go4lunch.services.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailsResponse(
        @SerializedName("html_attributions")
        var htmlAttributions: List<Any>? = null,

        @SerializedName("result")
        var detailsResult: DetailsResult? = null,
        var status: String? = null,
) : Serializable {

    override fun toString(): String {
        return "DetailsResponse{" +
                "htmlAttributions=" + htmlAttributions +
                ", detailsResult=" + detailsResult +
                ", status='" + status + '\'' +
                '}'
    }
}