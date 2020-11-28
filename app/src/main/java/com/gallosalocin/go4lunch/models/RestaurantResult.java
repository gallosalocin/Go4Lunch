package com.gallosalocin.go4lunch.models;

import com.gallosalocin.go4lunch.services.dto.RestaurantGeometry;
import com.gallosalocin.go4lunch.services.dto.RestaurantOpeningHours;
import com.gallosalocin.go4lunch.services.dto.RestaurantPhoto;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RestaurantResult implements Serializable {

    private RestaurantGeometry geometry;
    private String name;
    @SerializedName("opening_hours")
    private RestaurantOpeningHours restaurantOpeningHours;
    @SerializedName("vicinity")
    private String address;
    @SerializedName("place_id")
    private String placeId;
    @SerializedName("photos")
    private List<RestaurantPhoto> restaurantPhotos;
    private float rating;

    private int distance;
    private int workmates;



    public RestaurantResult() {
    }

    public RestaurantResult(RestaurantGeometry geometry, String name, RestaurantOpeningHours restaurantOpeningHours, String address, String placeId,
                            List<RestaurantPhoto> restaurantPhotos, float rating, int workmates, int distance) {
        this.geometry = geometry;
        this.name = name;
        this.restaurantOpeningHours = restaurantOpeningHours;
        this.address = address;
        this.placeId = placeId;
        this.restaurantPhotos = restaurantPhotos;
        this.rating = rating;
        this.workmates = workmates;
        this.distance = distance;
    }

    public RestaurantResult(String name, RestaurantOpeningHours restaurantOpeningHours, String address, String placeId,
                            List<RestaurantPhoto> restaurantPhotos, float rating, int workmates, int distance) {
        this.name = name;
        this.restaurantOpeningHours = restaurantOpeningHours;
        this.address = address;
        this.placeId = placeId;
        this.restaurantPhotos = restaurantPhotos;
        this.rating = rating;
        this.workmates = workmates;
        this.distance = distance;
    }

    public RestaurantResult(String name, float rating, int workmates, int distance) {
        this.name = name;
        this.rating = rating;
        this.workmates = workmates;
        this.distance = distance;
    }

    public RestaurantResult(String name, String address, float rating, int workmates, int distance) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.workmates = workmates;
        this.distance = distance;
    }

    public RestaurantGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(RestaurantGeometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestaurantOpeningHours getRestaurantOpeningHours() {
        return restaurantOpeningHours;
    }

    public void setRestaurantOpeningHours(RestaurantOpeningHours restaurantOpeningHours) {
        this.restaurantOpeningHours = restaurantOpeningHours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<RestaurantPhoto> getRestaurantPhotos() {
        return restaurantPhotos;
    }

    public void setRestaurantPhotos(List<RestaurantPhoto> restaurantPhotos) {
        this.restaurantPhotos = restaurantPhotos;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getWorkmates() {
        return workmates;
    }

    public void setWorkmates(int workmates) {
        this.workmates = workmates;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int  distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "RestaurantResult{" +
                "name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", rating=" + rating +
                '}';
    }
}
