package com.gallosalocin.go4lunch.data.network

import com.gallosalocin.go4lunch.data.network.dto.DetailsResponse
import com.gallosalocin.go4lunch.data.network.dto.RestaurantResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantApi {

    @GET("nearbysearch/json?")
    fun getNearbyRestaurant(
            @Query("location") location: String,
            @Query("radius") radius: Int,
            @Query("type") type: String,
            @Query("key") key: String
    ): Call<RestaurantResponse>

    @GET("details/json?")
    fun getDetailsRestaurant(
            @Query("place_id") placeId: String,
            @Query("key") key: String
    ): Call<DetailsResponse>
}