package com.gallosalocin.go4lunch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.services.RestaurantApi
import com.gallosalocin.go4lunch.services.dto.DetailsResponse
import com.gallosalocin.go4lunch.services.dto.DetailsResult
import com.gallosalocin.go4lunch.services.dto.RestaurantResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
        application: Application,
        private var restaurantApi: RestaurantApi

) : AndroidViewModel(application) {

    private lateinit var restaurantList: MutableLiveData<List<RestaurantResult>>
    private lateinit var detailsResult: MutableLiveData<DetailsResult>

    fun getRestaurants(currentLocation: String, radius: Int, type: String, key: String): LiveData<List<RestaurantResult>> {
        restaurantList = MutableLiveData()
        loadRestaurants(currentLocation, radius, type, key)
        return restaurantList
    }

    fun getDetailsRestaurant(placeId: String, key: String): LiveData<DetailsResult?> {
        detailsResult = MutableLiveData()
        loadDetailsRestaurant(placeId, key)
        return detailsResult
    }

    private fun loadRestaurants(currentLocation: String, radius: Int, type: String, key: String) {
        val restaurantResponseCall = restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key)
        restaurantResponseCall.enqueue(object : Callback<RestaurantResponse?> {
            override fun onResponse(call: Call<RestaurantResponse?>, response: Response<RestaurantResponse?>) {
                if (response.body() != null) {
                    restaurantList.value = response.body()!!.restaurantResults
                }
            }

            override fun onFailure(call: Call<RestaurantResponse?>, t: Throwable) {}
        })
    }

    private fun loadDetailsRestaurant(placeId: String, key: String) {
        val detailsResponseCall = restaurantApi.getDetailsRestaurant(placeId, key)
        detailsResponseCall.enqueue(object : Callback<DetailsResponse?> {
            override fun onResponse(call: Call<DetailsResponse?>, response: Response<DetailsResponse?>) {
                if (response.body() != null) {
                    detailsResult.value = response.body()!!.detailsResult
                }
            }

            override fun onFailure(call: Call<DetailsResponse?>, t: Throwable) {
                Timber.e("Error loadDetailsRestaurant : %s", t.message)
            }
        })
    }

}