package com.gallosalocin.go4lunch.ui.fragments;

import com.gallosalocin.go4lunch.models.RestaurantResult;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class ListViewFragmentTest {

    private RestaurantResult restaurantResultTest;
    private List<RestaurantResult> restaurantResultList;

    @Before
    public void setUp() {
        restaurantResultTest = new RestaurantResult();

        restaurantResultList = new ArrayList<>();
        restaurantResultList.add(new RestaurantResult("name 1", 2f, 3, 4));
        restaurantResultList.add(new RestaurantResult("name 2", 3f, 4, 1));
        restaurantResultList.add(new RestaurantResult("name 3", 4f, 1, 2));
        restaurantResultList.add(new RestaurantResult("name 4", 1f, 2, 3));
    }

    @Test
    public void calculateRating_Success() {
        restaurantResultTest.setRating(5f);
        float result = restaurantResultTest.getRating() * 3 / 5;
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void calculateOccurences_Success() {
        restaurantResultTest.setPlaceId("placeId1 test");
        List<String> resultPlaceIdList = Arrays.asList("placeId1 test", "placeId1 test", "placeId2 test");
        int result = Collections.frequency(resultPlaceIdList, restaurantResultTest.getPlaceId());
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void sortListByName_AToZ_Success() {
        restaurantResultList.sort(comparing(RestaurantResult::getName));

        assertThat(restaurantResultList.get(0).getName()).isEqualTo("name 1");
        assertThat(restaurantResultList.get(1).getName()).isEqualTo("name 2");
        assertThat(restaurantResultList.get(2).getName()).isEqualTo("name 3");
        assertThat(restaurantResultList.get(3).getName()).isEqualTo("name 4");
    }

    @Test
    public void sortListByRating_BiggerToLower_Success() {
        restaurantResultList.sort(Comparator.comparing(RestaurantResult::getRating).reversed());

        assertThat(restaurantResultList.get(0).getRating()).isEqualTo(4f);
        assertThat(restaurantResultList.get(1).getRating()).isEqualTo(3f);
        assertThat(restaurantResultList.get(2).getRating()).isEqualTo(2f);
        assertThat(restaurantResultList.get(3).getRating()).isEqualTo(1f);
    }

    @Test
    public void sortListByNumberOfWorkmates_BiggerToLower_Success() {
        restaurantResultList.sort(comparingInt(RestaurantResult::getWorkmates).reversed());

        assertThat(restaurantResultList.get(0).getWorkmates()).isEqualTo(4);
        assertThat(restaurantResultList.get(1).getWorkmates()).isEqualTo(3);
        assertThat(restaurantResultList.get(2).getWorkmates()).isEqualTo(2);
        assertThat(restaurantResultList.get(3).getWorkmates()).isEqualTo(1);
    }

    @Test
    public void sortListByDistance_NearestToFarthest_Success() {
        restaurantResultList.sort(comparing(RestaurantResult::getDistance));

        assertThat(restaurantResultList.get(0).getDistance()).isEqualTo(1);
        assertThat(restaurantResultList.get(1).getDistance()).isEqualTo(2);
        assertThat(restaurantResultList.get(2).getDistance()).isEqualTo(3);
        assertThat(restaurantResultList.get(3).getDistance()).isEqualTo(4);
    }

}