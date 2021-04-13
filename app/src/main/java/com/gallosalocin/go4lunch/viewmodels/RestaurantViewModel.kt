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
import com.gallosalocin.go4lunch.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {

    private var restaurantApi: RestaurantApi? = null
    private var restaurantList: MutableLiveData<List<RestaurantResult>?>? = null
    private var detailsResult: MutableLiveData<DetailsResult?>? = null

    fun getRestaurants(currentLocation: String, radius: Int, type: String, key: String): LiveData<List<RestaurantResult>?> {
        if (restaurantList == null) {
            restaurantList = MutableLiveData()
            loadRestaurants(currentLocation, radius, type, key)
        }
        return restaurantList as MutableLiveData<List<RestaurantResult>?>
    }

    fun getDetailsRestaurant(placeId: String, key: String): LiveData<DetailsResult?> {
        detailsResult = MutableLiveData()
        loadDetailsRestaurant(placeId, key)
        return detailsResult as MutableLiveData<DetailsResult?>
    }

    private fun setupRestaurantApi() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build()
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        restaurantApi = retrofit.create(RestaurantApi::class.java)
    }

    private fun loadRestaurants(currentLocation: String, radius: Int, type: String, key: String) {
        setupRestaurantApi()
        val restaurantResponseCall = restaurantApi!!.getNearbyRestaurant(currentLocation, radius, type, key)
        restaurantResponseCall!!.enqueue(object : Callback<RestaurantResponse?> {
            override fun onResponse(call: Call<RestaurantResponse?>, response: Response<RestaurantResponse?>) {
                if (response.body() != null) {
                    restaurantList!!.value = response.body()!!.restaurantResults
                }
            }

            override fun onFailure(call: Call<RestaurantResponse?>, t: Throwable) {}
        })
    }

    private fun loadDetailsRestaurant(placeId: String, key: String) {
        setupRestaurantApi()
        val detailsResponseCall = restaurantApi!!.getDetailsRestaurant(placeId, key)
        detailsResponseCall!!.enqueue(object : Callback<DetailsResponse?> {
            override fun onResponse(call: Call<DetailsResponse?>, response: Response<DetailsResponse?>) {
                if (response.body() != null) {
                    detailsResult!!.value = response.body()!!.detailsResult
                }
            }

            override fun onFailure(call: Call<DetailsResponse?>, t: Throwable) {
                Timber.e("Error loadDetailsRestaurant : %s", t.message)
            }
        })
    }
}