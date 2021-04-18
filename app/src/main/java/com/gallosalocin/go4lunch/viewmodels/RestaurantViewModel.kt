package com.gallosalocin.go4lunch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.services.RestaurantApi
import com.gallosalocin.go4lunch.services.dto.DetailsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
        application: Application,
        private var restaurantApi: RestaurantApi
) : AndroidViewModel(application) {

    private lateinit var restaurantList: MutableLiveData<List<RestaurantResult>>
    private lateinit var detailsResult: MutableLiveData<DetailsResult>


    fun getNearbyRestaurantList(currentLocation: String, radius: Int, type: String, key: String): MutableLiveData<List<RestaurantResult>> {
        restaurantList = MutableLiveData()
        apiCallNearbyRestaurant(currentLocation, radius, type, key)
        return restaurantList
    }

    fun getDetailsRestaurant(placeId: String, key: String): LiveData<DetailsResult?> {
        detailsResult = MutableLiveData()
        apiCallDetailsRestaurant(placeId, key)
        return detailsResult
    }

//    private fun apiCallNearbyRestaurant(currentLocation: String, radius: Int, type: String, key: String) {
//        restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key)
//                .map { throw RuntimeException() }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        { value -> Timber.d("Test -> $value") },
//                        { error -> Timber.d("Test -> $error") },
//                )
//    }

    private fun apiCallNearbyRestaurant(currentLocation: String, radius: Int, type: String, key: String) {
        restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> restaurantList.postValue(value.restaurantResults) },
                        { error -> Timber.e("$error") },
                        { Timber.d("Completed") }
                )
    }

    private fun apiCallDetailsRestaurant(placeId: String, key: String) {
        restaurantApi.getDetailsRestaurant(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    detailsResult.postValue(it.detailsResult)
                }
    }
}