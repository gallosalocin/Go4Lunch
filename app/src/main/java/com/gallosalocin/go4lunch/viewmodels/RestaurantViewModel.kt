package com.gallosalocin.go4lunch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.gallosalocin.go4lunch.services.RestaurantApi
import com.gallosalocin.go4lunch.services.dto.DetailsResponse
import com.gallosalocin.go4lunch.services.dto.DetailsResult
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
        restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> restaurantList.postValue(value.restaurantResults) },
                        { error -> Timber.e("$error") },
                        { Timber.d("Test -> Completed") }
                )
    }

    private fun apiCallDetailsRestaurant(placeId: String, key: String) {
        Observable.create<Call<DetailsResponse>> {
            try {
                it?.onNext(restaurantApi.getDetailsRestaurant(placeId, key))
                it?.onComplete()
            } catch (error: Exception) {
                it?.onError(error)
                Timber.d("Test -> onError : $error")
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            it.enqueue(object : Callback<DetailsResponse?> {
                                override fun onResponse(call: Call<DetailsResponse?>, response: Response<DetailsResponse?>) {
                                    detailsResult.postValue(response.body()?.detailsResult)
                                }

                                override fun onFailure(call: Call<DetailsResponse?>, throwable: Throwable) {
                                    Timber.d("Test -> onFailure : ${throwable.message}")
                                }
                            })
                        },
                        { error -> Timber.e("Test -> onError : $error") },
                        { Timber.d("Test -> onComplete : called") }
                )
    }
}