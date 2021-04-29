package com.gallosalocin.go4lunch.data

import com.gallosalocin.go4lunch.data.network.RestaurantApi
import com.gallosalocin.go4lunch.data.network.dto.DetailsResponse
import com.gallosalocin.go4lunch.data.network.dto.RestaurantResponse
import retrofit2.Call
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
        private val restaurantApi: RestaurantApi
) {

    fun getNearbyRestaurants(currentLocation: String, radius: Int, type: String, key: String): Call<RestaurantResponse> =
            restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key)

    fun getDetailsRestaurant(placeId: String, key: String): Call<DetailsResponse> =
            restaurantApi.getDetailsRestaurant(placeId, key)

}