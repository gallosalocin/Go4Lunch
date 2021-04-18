package com.gallosalocin.go4lunch.models;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class WorkmateTest {

    private Workmate workmate;

    @Before
    public void setup() {
        workmate = new Workmate();
    }

    @Test
    public void setNameSameGetName_Success() {
        String name = "test name";
        workmate.setName(name);
        String result = workmate.getName();
        assertThat(name).isEqualTo(result);
    }

    @Test
    public void setImageSameGetImage_Success() {
        String image = "test image";
        workmate.setImage(image);
        String result = workmate.getImage();
        assertThat(image).isEqualTo(result);
    }

    @Test
    public void setChosenRestaurantIdSameGetChosenRestaurantId_Success() {
        String chosenRestaurantId = "test restaurant id";
        workmate.setChosenRestaurantId(chosenRestaurantId);
        String result = workmate.getChosenRestaurantId();
        assertThat(chosenRestaurantId).isEqualTo(result);
    }

    @Test
    public void setChosenRestaurantNameSameGetChosenRestaurantName_Success() {
        String chosenRestaurantName = "test restaurant name";
        workmate.setChosenRestaurantName(chosenRestaurantName);
        String result = workmate.getChosenRestaurantName();
        assertThat(chosenRestaurantName).isEqualTo(result);
    }

    @Test
    public void setFavoriteSameGetFavorite_Success() {
        List<String> favorite = Arrays.asList("favorite 1", "favorite 2");
        workmate.setFavorite(favorite);
        List<String> result = workmate.getFavorite();
        assertThat(favorite).isEqualTo(result);
    }
}