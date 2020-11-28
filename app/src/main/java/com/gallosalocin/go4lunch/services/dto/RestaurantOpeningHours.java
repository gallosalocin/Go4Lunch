package com.gallosalocin.go4lunch.services.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RestaurantOpeningHours implements Serializable {

    @SerializedName("open_now")
    private boolean openNow;

    public RestaurantOpeningHours(boolean openNow) {
        this.openNow = openNow;
    }

    public RestaurantOpeningHours() {
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    @Override
    public String toString() {
        return "RestaurantOpeningHours{" +
                "openNow=" + openNow +
                '}';
    }
}

