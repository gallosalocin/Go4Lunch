package com.gallosalocin.go4lunch.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gallosalocin.go4lunch.services.dto.DetailsResult
import com.gallosalocin.go4lunch.services.dto.RestaurantGeometry
import com.gallosalocin.go4lunch.services.dto.RestaurantLocation
import com.gallosalocin.go4lunch.services.dto.RestaurantOpeningHours
import com.google.common.truth.Truth.assertThat
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RestaurantViewModelTest {

    private val currentLocation = "current location test"
    private val radius = 500
    private val type = "type test"
    private val key = "key test"
    private val placeId = "placeId test"

    private lateinit var detailsResultPublishSubject: PublishSubject<DetailsResult>
    private lateinit var apiCallDetailsResult: DetailsResult

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var restaurantViewModelTest: RestaurantViewModel

    @Before
    fun setUp() {
        restaurantViewModelTest = Mockito.mock(RestaurantViewModel::class.java)

        apiCallDetailsResult = DetailsResult(
                formattedPhoneNumber = "phoneNumber test",
                name = "name test",
                rating = 5F,
                address = "address test",
                website = "website test",
                detailsGeometry = RestaurantGeometry(RestaurantLocation(0f, 0f)),
                detailsPhotos = listOf(),
                restaurantOpeningHours = RestaurantOpeningHours(false)
        )
    }

    private fun mockedObservableCompletedWithResult(): Observable<DetailsResult> =
            Observable.create {
                it.onNext(apiCallDetailsResult)
                it.onComplete()
            }

    private fun mockedObservableError(): Observable<Int> = Observable.error(RuntimeException())

    @Test
    fun testObservableCompletedWithResult() {
        mockedObservableCompletedWithResult()
                .test()
                .assertValues(apiCallDetailsResult)
    }

    @Test
    fun testObservableError() {
        mockedObservableError()
                .test()
                .assertFailure(RuntimeException::class.java)
    }


    // Ici je n'arrive pas à voir comment pouvoir tester le getDetailsRestaurant() qui retourne detailsResultPublishSubject...
    @Test
    fun getDetailsRestaurant() {
        detailsResultPublishSubject = PublishSubject.create()

        mockedObservableCompletedWithResult()
                .subscribe {
                    detailsResultPublishSubject.onNext(it)
                }

        Mockito.doReturn(detailsResultPublishSubject).`when`(restaurantViewModelTest.getDetailsRestaurant(placeId, key))

    }

}