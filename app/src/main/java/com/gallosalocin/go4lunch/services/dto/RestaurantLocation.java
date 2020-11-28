package com.gallosalocin.go4lunch.services.dto;

import java.io.Serializable;

public class RestaurantLocation implements Serializable {

    private float lat;
    private float lng;

    public RestaurantLocation() {
    }

    public RestaurantLocation(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "RestaurantLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
