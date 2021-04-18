package com.gallosalocin.go4lunch.ui.fragments

import com.gallosalocin.go4lunch.models.RestaurantResult
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class ListViewFragmentTest {

    private lateinit var restaurantResultTest: RestaurantResult
    private lateinit var restaurantResultList: MutableList<RestaurantResult>

    @Before
    fun setUp() {
        restaurantResultTest = RestaurantResult()
        restaurantResultList = ArrayList()
        restaurantResultList.add(RestaurantResult(name = "name 1", rating = 2f, workmates = 3F, distance = 4))
        restaurantResultList.add(RestaurantResult(name = "name 2", rating = 3f, workmates = 4F, distance = 1))
        restaurantResultList.add(RestaurantResult(name = "name 3", rating = 4f, workmates = 1F, distance = 2))
        restaurantResultList.add(RestaurantResult(name = "name 4", rating = 1f, workmates = 2F, distance = 3))
    }

    @Test
    fun calculateRating_Success() {
        restaurantResultTest.rating = 5f
        val result = restaurantResultTest.rating * 3 / 5
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun calculateOccurences_Success() {
        restaurantResultTest.placeId = "placeId1 test"
        val resultPlaceIdList = listOf("placeId1 test", "placeId1 test", "placeId2 test")
        val result = Collections.frequency(resultPlaceIdList, restaurantResultTest.placeId)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun sortListByName_AToZ_Success() {
        val restaurantFiltered = restaurantResultList.sortedBy { it.name?.toLowerCase() }
        assertThat(restaurantFiltered[0].name).isEqualTo("name 1")
        assertThat(restaurantFiltered[1].name).isEqualTo("name 2")
        assertThat(restaurantFiltered[2].name).isEqualTo("name 3")
        assertThat(restaurantFiltered[3].name).isEqualTo("name 4")
    }

    @Test
    fun sortListByRating_BiggerToLower_Success() {
        val restaurantFiltered = restaurantResultList.sortedBy { it.rating }.reversed()
        assertThat(restaurantFiltered[0].rating).isEqualTo(4f)
        assertThat(restaurantFiltered[1].rating).isEqualTo(3f)
        assertThat(restaurantFiltered[2].rating).isEqualTo(2f)
        assertThat(restaurantFiltered[3].rating).isEqualTo(1f)
    }

    @Test
    fun sortListByNumberOfWorkmates_BiggerToLower_Success() {
        val restaurantFiltered = restaurantResultList.sortedBy { it.workmates }.reversed()
        assertThat(restaurantFiltered[0].workmates).isEqualTo(4)
        assertThat(restaurantFiltered[1].workmates).isEqualTo(3)
        assertThat(restaurantFiltered[2].workmates).isEqualTo(2)
        assertThat(restaurantFiltered[3].workmates).isEqualTo(1)
    }

    @Test
    fun sortListByDistance_NearestToFarthest_Success() {
        val restaurantFiltered = restaurantResultList.sortedBy { it.distance }
        assertThat(restaurantFiltered[0].distance).isEqualTo(1)
        assertThat(restaurantFiltered[1].distance).isEqualTo(2)
        assertThat(restaurantFiltered[2].distance).isEqualTo(3)
        assertThat(restaurantFiltered[3].distance).isEqualTo(4)
    }
}