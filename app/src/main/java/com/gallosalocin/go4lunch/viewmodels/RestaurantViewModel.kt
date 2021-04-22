package com.gallosalocin.go4lunch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.services.RestaurantApi
import com.gallosalocin.go4lunch.services.dto.DetailsResponse
import com.gallosalocin.go4lunch.services.dto.DetailsResult
import com.gallosalocin.go4lunch.services.dto.RestaurantResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
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


    fun getNearbyRestaurantList(currentLocation: String, radius: Int, type: String, key: String): MutableLiveData<List<RestaurantResult>> {
        restaurantList = MutableLiveData()
        apiCallNearbyRestaurant(currentLocation, radius, type, key)
        return restaurantList
    }

    fun getDetailsRestaurant(placeId: String, key: String): MutableLiveData<DetailsResult> {
        detailsResult = MutableLiveData()
        apiCallDetailsRestaurant(placeId, key)
        return detailsResult
    }

    private fun apiCallNearbyRestaurant(currentLocation: String, radius: Int, type: String, key: String) {
        Observable.create<List<RestaurantResult>> {
            restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key).enqueue(object : Callback<RestaurantResponse> {
                override fun onResponse(call: Call<RestaurantResponse>, response: Response<RestaurantResponse>) {
                    it.onNext(response.body()?.restaurantResults)
                }

                override fun onFailure(call: Call<RestaurantResponse>, throwable: Throwable) {
                    Timber.e("$throwable")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { restaurantList.postValue(it) },
                        { error -> Timber.e("$error") },
                )
    }

    private fun apiCallDetailsRestaurant(placeId: String, key: String) {
        Observable.create<DetailsResult> {
            restaurantApi.getDetailsRestaurant(placeId, key).enqueue(object : Callback<DetailsResponse?> {
                override fun onResponse(call: Call<DetailsResponse?>, response: Response<DetailsResponse?>) {
                    it.onNext(response.body()?.detailsResult)
                }

                override fun onFailure(call: Call<DetailsResponse?>, throwable: Throwable) {
                    Timber.e("$throwable")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { detailsResult.postValue(it) },
                        { error -> Timber.e("Test -> onError : $error") }
                )
    }
}