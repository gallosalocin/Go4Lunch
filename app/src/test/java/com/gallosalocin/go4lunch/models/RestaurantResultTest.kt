package com.gallosalocin.go4lunch.models

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RestaurantResultTest {

    private lateinit var restaurantResult: RestaurantResult

    @Before
    fun setup() {
        restaurantResult = RestaurantResult()
    }

    @Test
    fun setName_GetName_ReturnSameName() {
        val name = "test name"
        restaurantResult.name = name
        val result = restaurantResult.name
        assertThat(name).isEqualTo(result)
    }

    @Test
    fun setAddress_GetAddress_ReturnSameAddress() {
        val address = "test address"
        restaurantResult.address = address
        val result = restaurantResult.address
        assertThat(address).isEqualTo(result)
    }

    @Test
    fun setPlaceId_GetPlaceId_ReturnSamePlaceId() {
        val placeId = "test placeId"
        restaurantResult.placeId = placeId
        val result = restaurantResult.placeId
        assertThat(placeId).isEqualTo(result)
    }

    @Test
    fun setRating_GetRating_ReturnSameRating() {
        val rating = 123f
        restaurantResult.rating = rating
        val result = restaurantResult.rating
        assertThat(rating).isEqualTo(result)
    }

    @Test
    fun setDistance_GetDistance_ReturnSameDistance() {
        val distance = 123
        restaurantResult.distance = distance
        val result = restaurantResult.distance
        assertThat(distance).isEqualTo(result)
    }

    @Test
    fun setWorkmates_GetWorkmates_ReturnSameWorkmates() {
        val workmates = 123
        restaurantResult.workmates = workmates.toFloat()
        val result = restaurantResult.workmates.toInt()
        assertThat(workmates).isEqualTo(result)
    }
}