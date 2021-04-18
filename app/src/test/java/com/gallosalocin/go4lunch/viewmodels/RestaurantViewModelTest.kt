package com.gallosalocin.go4lunch.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.services.dto.DetailsResult
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RestaurantViewModelTest {

    private val currentLocation = "current location test"
    private val radius = 500
    private val type = "type test"
    private val key = "key test"
    private val placeId = "placeId test"
    private val liveDataResponseList = MutableLiveData<List<RestaurantResult>>()
    private val restaurantResultList: MutableList<RestaurantResult> = mutableListOf()
    private lateinit var restaurantResult: RestaurantResult
    private lateinit var detailsResult: DetailsResult
    private lateinit var detailsResultMutableLiveData: MutableLiveData<DetailsResult>

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var restaurantViewModelTest: RestaurantViewModel

    @Before
    fun setUp() {
        restaurantViewModelTest = Mockito.mock(RestaurantViewModel::class.java)
        restaurantResult = RestaurantResult(
                name = "name test1",
                address = "address test1",
                rating = 1F,
                workmates = 1F,
                distance = 100
        )
        detailsResult = DetailsResult(
                formattedPhoneNumber = "phoneNumber test",
                name = "name test",
                rating = 5F,
                address = "address test",
                website = "website test"
        )
        detailsResultMutableLiveData = MutableLiveData()
    }

    @Test
    fun callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectName() {
        // Given
        restaurantResultList.add(restaurantResult)
        liveDataResponseList.value = restaurantResultList
        // When
        Mockito.`when`(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)).thenReturn(liveDataResponseList)
        // Then
        val listLiveData: LiveData<List<RestaurantResult>> = restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)
        val (_, name) = listLiveData.value!![0]
        val firstResponseList = liveDataResponseList.value!![0]
        assertThat(name).isEqualTo(firstResponseList.name)
    }

    @Test
    fun callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectAddress() {
        // Given
        restaurantResultList.add(restaurantResult)
        liveDataResponseList.value = restaurantResultList
        // When
        Mockito.`when`(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)).thenReturn(liveDataResponseList)
        // Then
        val listLiveData: LiveData<List<RestaurantResult>> = restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)
        val (_, _, _, address) = listLiveData.value!![0]
        val firstResponseList = liveDataResponseList.value!![0]
        assertThat(address).isEqualTo(firstResponseList.address)
    }

    @Test
    fun callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectRating() {
        // Given
        restaurantResultList.add(restaurantResult)
        liveDataResponseList.value = restaurantResultList
        // When
        Mockito.`when`(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)).thenReturn(liveDataResponseList)
        // Then
        val listLiveData: LiveData<List<RestaurantResult>> = restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)
        val (_, _, _, _, _, _, rating) = listLiveData.value!![0]
        val firstResponseList = liveDataResponseList.value!![0]
        assertThat(rating).isEqualTo(firstResponseList.rating)
    }

    @Test
    fun callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectWorkmates() {
        // Given
        restaurantResultList.add(restaurantResult)
        liveDataResponseList.value = restaurantResultList
        // When
        Mockito.`when`(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)).thenReturn(liveDataResponseList)
        // Then
        val listLiveData: LiveData<List<RestaurantResult>> = restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)
        val (_, _, _, _, _, _, _, _, workmates) = listLiveData.value!![0]
        val firstResponseList = liveDataResponseList.value!![0]
        assertThat(workmates).isEqualTo(firstResponseList.workmates)
    }

    @Test
    fun callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectDistance() {
        // Given
        restaurantResultList.add(restaurantResult)
        liveDataResponseList.value = restaurantResultList
        // When
        Mockito.`when`(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)).thenReturn(liveDataResponseList)
        // Then
        val listLiveData: LiveData<List<RestaurantResult>> = restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, key)
        val (_, _, _, _, _, _, _, distance) = listLiveData.value!![0]
        val firstResponseList = liveDataResponseList.value!![0]
        assertThat(distance).isEqualTo(firstResponseList.distance)
    }

    @Test
    fun callNearbySearchApi_WithWrongKey_ReturnError() {
        Mockito.`when`(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, "wrong key")).thenReturn(null)
        assertThat(restaurantViewModelTest.getNearbyRestaurantList(currentLocation, radius, type, "wrong key")).isNull()
    }

    @Test
    fun callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectPhoneNumber() {
        // Given
        detailsResultMutableLiveData.value = detailsResult
        // When
        Mockito.`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData)
        // Then
        val liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key)
        assertThat(liveData.value!!.formattedPhoneNumber).isEqualTo(detailsResultMutableLiveData.value!!.formattedPhoneNumber)
    }

    @Test
    fun callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectName() {
        // Given
        detailsResultMutableLiveData.value = detailsResult
        // When
        Mockito.`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData)
        // Then
        val liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key)
        assertThat(liveData.value!!.name).isEqualTo(detailsResultMutableLiveData.value!!.name)
    }

    @Test
    fun callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectRating() {
        // Given
        detailsResultMutableLiveData.value = detailsResult
        // When
        Mockito.`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData)
        // Then
        val liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key)
        assertThat(liveData.value!!.rating).isEqualTo(detailsResultMutableLiveData.value!!.rating)
    }

    @Test
    fun callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectAddress() {
        // Given
        detailsResultMutableLiveData.value = detailsResult
        // When
        Mockito.`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData)
        // Then
        val liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key)
        assertThat(liveData.value!!.address).isEqualTo(detailsResultMutableLiveData.value!!.address)
    }

    @Test
    fun callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectWebsite() {
        // Given
        detailsResultMutableLiveData.value = detailsResult
        // When
        Mockito.`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData)
        // Then
        val liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key)
        assertThat(liveData.value!!.website).isEqualTo(detailsResultMutableLiveData.value!!.website)
    }

    @Test
    fun callDetailsApi_WithWrongKey_ReturnError() {
        Mockito.`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, "wrong key")).thenReturn(null)
        assertThat(restaurantViewModelTest.getDetailsRestaurant(placeId, "wrong key")).isNull()
    }
}