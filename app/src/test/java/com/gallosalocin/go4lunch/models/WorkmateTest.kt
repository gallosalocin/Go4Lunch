package com.gallosalocin.go4lunch.models

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class WorkmateTest {

    private lateinit var workmate: Workmate

    @Before
    fun setup() {
        workmate = Workmate()
    }

    @Test
    fun setNameSameGetName_Success() {
        val name = "test name"
        workmate.name = name
        val result = workmate.name
        assertThat(name).isEqualTo(result)
    }

    @Test
    fun setImageSameGetImage_Success() {
        val image = "test image"
        workmate.image = image
        val result = workmate.image
        assertThat(image).isEqualTo(result)
    }

    @Test
    fun setChosenRestaurantIdSameGetChosenRestaurantId_Success() {
        val chosenRestaurantId = "test restaurant id"
        workmate.chosenRestaurantId = chosenRestaurantId
        val result = workmate.chosenRestaurantId
        assertThat(chosenRestaurantId).isEqualTo(result)
    }

    @Test
    fun setChosenRestaurantNameSameGetChosenRestaurantName_Success() {
        val chosenRestaurantName = "test restaurant name"
        workmate.chosenRestaurantName = chosenRestaurantName
        val result = workmate.chosenRestaurantName
        assertThat(chosenRestaurantName).isEqualTo(result)
    }

    @Test
    fun setFavoriteSameGetFavorite_Success() {
        val favorite = listOf("favorite 1", "favorite 2")
        workmate.favorite = favorite
        val result = workmate.favorite
        assertThat(favorite).isEqualTo(result)
    }
}