package com.gallosalocin.go4lunch.services.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DetailsResult implements Serializable {

    @SerializedName("formatted_phone_number")
    private String formattedPhoneNumber;
    @SerializedName("opening_hours")
    private RestaurantOpeningHours restaurantOpeningHours;
    @SerializedName("geometry")
    private RestaurantGeometry detailsGeometry;
    private String name;
    @SerializedName("photos")
    private List<RestaurantPhoto> detailsPhotos;
    private float rating;
    @SerializedName("vicinity")
    private String address;
    private String website;

    public DetailsResult() {
    }

    public DetailsResult(String formattedPhoneNumber, RestaurantOpeningHours restaurantOpeningHours, RestaurantGeometry detailsGeometry, String name,
                         List<RestaurantPhoto> detailsPhotos, float rating, String address, String website) {
        this.formattedPhoneNumber = formattedPhoneNumber;
        this.restaurantOpeningHours = restaurantOpeningHours;
        this.detailsGeometry = detailsGeometry;
        this.name = name;
        this.detailsPhotos = detailsPhotos;
        this.rating = rating;
        this.address = address;
        this.website = website;
    }

    public DetailsResult(String formattedPhoneNumber, String name, float rating, String address, String website) {
        this.formattedPhoneNumber = formattedPhoneNumber;
        this.name = name;
        this.rating = rating;
        this.address = address;
        this.website = website;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public RestaurantOpeningHours getRestaurantOpeningHours() {
        return restaurantOpeningHours;
    }

    public void setRestaurantOpeningHours(RestaurantOpeningHours restaurantOpeningHours) {
        this.restaurantOpeningHours = restaurantOpeningHours;
    }

    public RestaurantGeometry getDetailsGeometry() {
        return detailsGeometry;
    }

    public void setDetailsGeometry(RestaurantGeometry detailsGeometry) {
        this.detailsGeometry = detailsGeometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RestaurantPhoto> getDetailsPhotos() {
        return detailsPhotos;
    }

    public void setDetailsPhotos(List<RestaurantPhoto> detailsPhotos) {
        this.detailsPhotos = detailsPhotos;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "DetailsResult{" +
                "formattedPhoneNumber='" + formattedPhoneNumber + '\'' +
                ", detailsGeometry=" + detailsGeometry +
                ", name='" + name + '\'' +
                ", detailsPhotos=" + detailsPhotos +
                ", rating=" + rating +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
