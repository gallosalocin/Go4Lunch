package com.gallosalocin.go4lunch.models

import com.gallosalocin.go4lunch.services.dto.RestaurantGeometry
import com.gallosalocin.go4lunch.services.dto.RestaurantLocation
import com.gallosalocin.go4lunch.services.dto.RestaurantOpeningHours
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RestaurantResultTest {

    private lateinit var restaurantResult: RestaurantResult

    @Before
    fun setup() {
        restaurantResult = RestaurantResult(
                name = "",
                address = "",
                placeId = "",
                distance = 0,
                rating = 0F,
                workmates = 0F,
                restaurantOpeningHours = RestaurantOpeningHours(false),
                geometry = RestaurantGeometry(RestaurantLocation(0f, 0f)),
                restaurantPhotos = listOf()
        )
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