package com.gallosalocin.go4lunch.services;

import com.gallosalocin.go4lunch.services.dto.DetailsResponse;
import com.gallosalocin.go4lunch.services.dto.RestaurantResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantApi {

    @GET("nearbysearch/json?")
    Call<RestaurantResponse> getNearbyRestaurant(@Query("location") String location,
                                                 @Query("radius") int radius,
                                                 @Query("type") String type,
                                                 @Query("key") String key);

    @GET("details/json?")
    Call<DetailsResponse> getDetailsRestaurant(@Query("place_id") String placeId,
                                               @Query("key") String key);
}
