package com.gallosalocin.go4lunch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.gallosalocin.go4lunch.data.network.dto.DetailsResponse
import com.gallosalocin.go4lunch.data.network.dto.DetailsResult
import com.gallosalocin.go4lunch.data.network.dto.RestaurantResponse
import com.gallosalocin.go4lunch.data.repositories.Repository
import com.gallosalocin.go4lunch.models.RestaurantResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class RestaurantViewModel @Inject constructor(
        application: Application,
        private var repository: Repository
) : AndroidViewModel(application) {

    private lateinit var disposable: Disposable

    private lateinit var restaurantListPS: PublishSubject<List<RestaurantResult>>

    var detailsResultPS: PublishSubject<DetailsResult> = PublishSubject.create()

    fun getNearbyRestaurantList(currentLocation: String, radius: Int, type: String, key: String): PublishSubject<List<RestaurantResult>> {
        restaurantListPS = PublishSubject.create()
        apiCallNearbyRestaurant(currentLocation, radius, type, key)
        return restaurantListPS
    }

    private fun apiCallNearbyRestaurant(currentLocation: String, radius: Int, type: String, key: String) {
        disposable = Observable.create<List<RestaurantResult>> {
            repository.remote.getNearbyRestaurants(currentLocation, radius, type, key).enqueue(object : Callback<RestaurantResponse> {
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
                        { restaurantListPS.onNext(it) },
                        { error -> Timber.e("$error") },
                )
    }

    fun apiCallDetailsRestaurant(placeId: String, key: String) {
        disposable = Single.create<DetailsResult> {
            repository.remote.getDetailsRestaurant(placeId, key).enqueue(object : Callback<DetailsResponse?> {
                override fun onResponse(call: Call<DetailsResponse?>, response: Response<DetailsResponse?>) {
                    it.onSuccess(response.body()?.detailsResult)
//                    it.onNext(response.body()?.detailsResult)
                }

                override fun onFailure(call: Call<DetailsResponse?>, throwable: Throwable) {
                    Timber.e("$throwable")
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { detailsResultPS.onNext(it) },
                        { error -> Timber.e("$error") }
                )
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}