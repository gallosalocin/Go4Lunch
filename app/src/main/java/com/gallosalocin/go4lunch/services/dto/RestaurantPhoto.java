package com.gallosalocin.go4lunch.services.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RestaurantPhoto implements Serializable {

    private int height;
    @SerializedName("photo_reference")
    private String photoReference;
    private int width;

    public RestaurantPhoto() {
    }

    public RestaurantPhoto(int height, String photoReference, int width) {
        this.height = height;
        this.photoReference = photoReference;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "height=" + height +
                ", photo_reference='" + photoReference + '\'' +
                ", width=" + width +
                '}';
    }
}
