package com.gallosalocin.go4lunch.services

import com.gallosalocin.go4lunch.services.dto.DetailsResponse
import com.gallosalocin.go4lunch.services.dto.RestaurantResponse
import io.reactivex.rxjava3.core.Observable
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
    ): Observable<RestaurantResponse>

    @GET("details/json?")
    fun getDetailsRestaurant(
            @Query("place_id") placeId: String,
            @Query("key") key: String
    ): Call<DetailsResponse>
}