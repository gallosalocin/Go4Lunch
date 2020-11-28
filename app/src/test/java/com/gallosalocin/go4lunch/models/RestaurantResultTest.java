package com.gallosalocin.go4lunch.models;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class RestaurantResultTest {

    private RestaurantResult restaurantResult;

    @Before
    public void setup() {
        restaurantResult = new RestaurantResult();
    }

    @Test
    public void setName_GetName_ReturnSameName() {
        String name = "test name";
        restaurantResult.setName(name);
        String result = restaurantResult.getName();
        assertThat(name).isEqualTo(result);
    }

    @Test
    public void setAddress_GetAddress_ReturnSameAddress() {
        String address = "test address";
        restaurantResult.setAddress(address);
        String result = restaurantResult.getAddress();
        assertThat(address).isEqualTo(result);
    }

    @Test
    public void setPlaceId_GetPlaceId_ReturnSamePlaceId() {
        String placeId = "test placeId";
        restaurantResult.setPlaceId(placeId);
        String result = restaurantResult.getPlaceId();
        assertThat(placeId).isEqualTo(result);
    }

    @Test
    public void setRating_GetRating_ReturnSameRating() {
        float rating = 123f;
        restaurantResult.setRating(rating);
        float result = restaurantResult.getRating();
        assertThat(rating).isEqualTo(result);
    }

    @Test
    public void setDistance_GetDistance_ReturnSameDistance() {
        int distance = 123;
        restaurantResult.setDistance(distance);
        int result = restaurantResult.getDistance();
        assertThat(distance).isEqualTo(result);
    }

    @Test
    public void setWorkmates_GetWorkmates_ReturnSameWorkmates() {
        int workmates = 123;
        restaurantResult.setWorkmates(workmates);
        int result = restaurantResult.getWorkmates();
        assertThat(workmates).isEqualTo(result);
    }
}