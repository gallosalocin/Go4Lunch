package com.gallosalocin.go4lunch.services.dto;

import com.gallosalocin.go4lunch.models.RestaurantResult;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RestaurantResponse implements Serializable {

    @SerializedName("html_attributions")
    private List<Object> htmlAttributions = null;
    @SerializedName("next_page_token")
    private String nextPageToken;
    @SerializedName("results")
    private List<RestaurantResult> restaurantResults;
    private String status;

    public RestaurantResponse() {
    }

    public RestaurantResponse(List<Object> htmlAttributions, String nextPageToken, List<RestaurantResult> restaurantResults, String status) {
        this.htmlAttributions = htmlAttributions;
        this.nextPageToken = nextPageToken;
        this.restaurantResults = restaurantResults;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<RestaurantResult> getRestaurantResults() {
        return restaurantResults;
    }

    public void setRestaurantResults(List<RestaurantResult> restaurantResults) {
        this.restaurantResults = restaurantResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RestaurantResponse{" +
                "htmlAttributions=" + htmlAttributions +
                ", nextPageToken='" + nextPageToken + '\'' +
                ", restaurantResults=" + restaurantResults +
                ", status='" + status + '\'' +
                '}';
    }
}
