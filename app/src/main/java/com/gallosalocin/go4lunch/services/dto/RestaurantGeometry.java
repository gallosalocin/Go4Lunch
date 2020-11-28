package com.gallosalocin.go4lunch.services.dto;

import java.io.Serializable;

public class RestaurantGeometry implements Serializable {

    private RestaurantLocation location;

    public RestaurantGeometry() {
    }

    public RestaurantGeometry(RestaurantLocation location) {
        this.location = location;
    }

    public RestaurantLocation getLocation() {
        return location;
    }

    public void setLocation(RestaurantLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RestaurantGeometry{" +
                "location=" + location +
                '}';
    }
}
