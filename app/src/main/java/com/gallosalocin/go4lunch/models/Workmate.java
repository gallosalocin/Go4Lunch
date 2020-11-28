package com.gallosalocin.go4lunch.models;

import java.util.List;

public class Workmate {
    private String name;
    private String image;
    private String chosenRestaurantId;
    private String chosenRestaurantName;
    private List<String> favorite;

    public Workmate(String name, String image, String chosenRestaurantId, String chosenRestaurantName, List<String> favorite) {
        this.name = name;
        this.image = image;
        this.chosenRestaurantId = chosenRestaurantId;
        this.chosenRestaurantName = chosenRestaurantName;
        this.favorite = favorite;
    }

    public Workmate() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    public void setChosenRestaurantId(String chosenRestaurantId) {
        this.chosenRestaurantId = chosenRestaurantId;
    }

    public String getChosenRestaurantName() {
        return chosenRestaurantName;
    }

    public void setChosenRestaurantName(String chosenRestaurantName) {
        this.chosenRestaurantName = chosenRestaurantName;
    }

    public List<String> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<String> favorite) {
        this.favorite = favorite;
    }
}



