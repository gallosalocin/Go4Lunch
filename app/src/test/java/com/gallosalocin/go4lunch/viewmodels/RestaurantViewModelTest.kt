package com.gallosalocin.go4lunch.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gallosalocin.go4lunch.data.network.RestaurantApi
import com.gallosalocin.go4lunch.data.network.dto.*
import com.google.common.truth.Truth
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.mock.Calls

@RunWith(MockitoJUnitRunner::class)
class RestaurantViewModelTest {

//    private val currentLocation = "current location test"
//    private val radius = 500
//    private val type = "type test"
//    private val key = "key test"
//    private val placeId = "placeId test"
//
//    private lateinit var testPublishSubject: PublishSubject<DetailsResult>
//    private lateinit var apiCallDetailsResult: DetailsResult
//    private lateinit var apiCallDetailsResultWrong: DetailsResult
//
//    private lateinit var detailsResponse: DetailsResponse
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @Mock
//    lateinit var restaurantViewModelTest: RestaurantViewModel
//
//    @Mock
//    lateinit var restaurantApi: RestaurantApi
//
//    @Before
//    fun setUp() {
//        restaurantApi = mock(RestaurantApi::class.java)
//
//        restaurantViewModelTest = RestaurantViewModel(mock(Application::class.java), restaurantApi)
//
//
//        apiCallDetailsResult = DetailsResult(
//                formattedPhoneNumber = "phoneNumber test",
//                name = "name test",
//                rating = 5F,
//                address = "address test",
//                website = "website test",
//                detailsGeometry = RestaurantGeometry(RestaurantLocation(0f, 0f)),
//                detailsPhotos = listOf(),
//                restaurantOpeningHours = RestaurantOpeningHours(false)
//        )
//
//        apiCallDetailsResultWrong = DetailsResult(
//                formattedPhoneNumber = "phoneNumber test",
//                name = "wrong name test",
//                rating = 5F,
//                address = "address test",
//                website = "website test",
//                detailsGeometry = RestaurantGeometry(RestaurantLocation(0f, 0f)),
//                detailsPhotos = listOf(),
//                restaurantOpeningHours = RestaurantOpeningHours(false)
//        )
//
//        detailsResponse = DetailsResponse(listOf(), apiCallDetailsResult, "test")
//    }
//
//    @Test
//    fun `test` () {
//        `when`(restaurantApi.getDetailsRestaurant(placeId, key)).thenReturn(Calls.response(detailsResponse))
//
//        restaurantViewModelTest.detailsResultPS
//                .subscribe {
//                    Truth.assertThat(it).isEqualTo(apiCallDetailsResult)
//                }
//
//        restaurantViewModelTest.apiCallDetailsRestaurant(placeId, key)
//
//        Mockito.verify(restaurantApi).getDetailsRestaurant(placeId, key)
//        Mockito.verifyNoMoreInteractions(restaurantApi)
//    }
//
//    private fun observableDetailsResult(): Observable<DetailsResult> {
//        `when`(restaurantApi.getDetailsRestaurant(placeId, key)).thenReturn(Calls.response(detailsResponse))
//
//        return Observable.create {
//            it.onNext(restaurantApi.getDetailsRestaurant(placeId, key).execute().body()?.detailsResult)
//            it.onComplete()
//        }
//    }
//
//    private fun publishSubject(): PublishSubject<DetailsResult> {
//        testPublishSubject = PublishSubject.create()
//
//        observableDetailsResult()
//                .subscribe {
//                    testPublishSubject.onNext(it)
//                }
//
//        return testPublishSubject
//    }
//
//    @Test
//    fun `observable emits DetailsResponse from restaurantApi getDetailsRestaurant()`() {
//        observableDetailsResult()
//                .test()
//                .assertResult(apiCallDetailsResult)
//    }
//
//    @Test
//    fun `test subject`() {
//        testPublishSubject = PublishSubject.create()
//        `when`(restaurantApi.getDetailsRestaurant(placeId, key)).thenReturn(Calls.response(detailsResponse))
//
//        Observable.create<DetailsResult> {
//            it.onNext(restaurantApi.getDetailsRestaurant(placeId, key).execute().body()?.detailsResult)
//            it.onComplete()
//        }
//                .subscribe {
//                    testPublishSubject.onNext(it)
//                }
//
//        testPublishSubject
//                .test()
//                .assertValues(apiCallDetailsResult)
//    }
}