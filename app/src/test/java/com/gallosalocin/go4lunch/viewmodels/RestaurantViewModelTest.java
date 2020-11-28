package com.gallosalocin.go4lunch.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gallosalocin.go4lunch.models.RestaurantResult;
import com.gallosalocin.go4lunch.services.dto.DetailsResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelTest {

    private final String currentLocation = "current location test";
    private final int radius = 500;
    private final String type = "type test";
    private final String key = "key test";
    private final String placeId = "placeId test";
    private final MutableLiveData<List<RestaurantResult>> liveDataResponseList = new MutableLiveData<>();
    private final List<RestaurantResult> restaurantResultList = new ArrayList<>();
    private RestaurantResult restaurantResult;
    private DetailsResult detailsResult;
    private MutableLiveData<DetailsResult> detailsResultMutableLiveData;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    RestaurantViewModel restaurantViewModelTest;

    @Before
    public void setUp() {
        restaurantViewModelTest = mock(RestaurantViewModel.class);

        restaurantResult = new RestaurantResult("name test1", "address test1", 1f, 1, 100);

        detailsResult = new DetailsResult("phoneNumber test", "name test", 5f, "address test", "website test");
        detailsResultMutableLiveData = new MutableLiveData<>();
    }

    @Test
    public void callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectName() {
        // Given
        restaurantResultList.add(restaurantResult);
        liveDataResponseList.setValue(restaurantResultList);
        // When
        when(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key)).thenReturn(liveDataResponseList);
        // Then
        LiveData<List<RestaurantResult>> listLiveData = restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key);
        RestaurantResult firstRestaurantResultList = listLiveData.getValue().get(0);
        RestaurantResult firstResponseList = liveDataResponseList.getValue().get(0);

        assertThat(firstRestaurantResultList.getName()).isEqualTo(firstResponseList.getName());
    }

    @Test
    public void callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectAddress() {
        // Given
        restaurantResultList.add(restaurantResult);
        liveDataResponseList.setValue(restaurantResultList);
        // When
        when(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key)).thenReturn(liveDataResponseList);
        // Then
        LiveData<List<RestaurantResult>> listLiveData = restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key);
        RestaurantResult firstRestaurantResultList = listLiveData.getValue().get(0);
        RestaurantResult firstResponseList = liveDataResponseList.getValue().get(0);

        assertThat(firstRestaurantResultList.getAddress()).isEqualTo(firstResponseList.getAddress());
    }

    @Test
    public void callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectRating() {
        // Given
        restaurantResultList.add(restaurantResult);
        liveDataResponseList.setValue(restaurantResultList);
        // When
        when(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key)).thenReturn(liveDataResponseList);
        // Then
        LiveData<List<RestaurantResult>> listLiveData = restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key);
        RestaurantResult firstRestaurantResultList = listLiveData.getValue().get(0);
        RestaurantResult firstResponseList = liveDataResponseList.getValue().get(0);

        assertThat(firstRestaurantResultList.getRating()).isEqualTo(firstResponseList.getRating());
    }

    @Test
    public void callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectWorkmates() {
        // Given
        restaurantResultList.add(restaurantResult);
        liveDataResponseList.setValue(restaurantResultList);
        // When
        when(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key)).thenReturn(liveDataResponseList);
        // Then
        LiveData<List<RestaurantResult>> listLiveData = restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key);
        RestaurantResult firstRestaurantResultList = listLiveData.getValue().get(0);
        RestaurantResult firstResponseList = liveDataResponseList.getValue().get(0);

        assertThat(firstRestaurantResultList.getWorkmates()).isEqualTo(firstResponseList.getWorkmates());
    }

    @Test
    public void callNearbySearchApi_RestaurantResponseSuccess_ReturnCorrectDistance() {
        // Given
        restaurantResultList.add(restaurantResult);
        liveDataResponseList.setValue(restaurantResultList);
        // When
        when(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key)).thenReturn(liveDataResponseList);
        // Then
        LiveData<List<RestaurantResult>> listLiveData = restaurantViewModelTest.getRestaurants(currentLocation, radius, type, key);
        RestaurantResult firstRestaurantResultList = listLiveData.getValue().get(0);
        RestaurantResult firstResponseList = liveDataResponseList.getValue().get(0);

        assertThat(firstRestaurantResultList.getDistance()).isEqualTo(firstResponseList.getDistance());
    }

    @Test
    public void callNearbySearchApi_WithWrongKey_ReturnError() {
        when(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, "wrong key")).thenReturn(null);
        assertThat(restaurantViewModelTest.getRestaurants(currentLocation, radius, type, "wrong key")).isNull();
    }

    @Test
    public void callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectPhoneNumber() {
        // Given
        detailsResultMutableLiveData.setValue(detailsResult);
        // When
        when(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData);
        // Then
        LiveData<DetailsResult> liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key);

        assertThat(liveData.getValue().getFormattedPhoneNumber()).isEqualTo(detailsResultMutableLiveData.getValue().getFormattedPhoneNumber());
    }

    @Test
    public void callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectName() {
        // Given
        detailsResultMutableLiveData.setValue(detailsResult);
        // When
        when(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData);
        // Then
        LiveData<DetailsResult> liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key);

        assertThat(liveData.getValue().getName()).isEqualTo(detailsResultMutableLiveData.getValue().getName());
    }

    @Test
    public void callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectRating() {
        // Given
        detailsResultMutableLiveData.setValue(detailsResult);
        // When
        when(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData);
        // Then
        LiveData<DetailsResult> liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key);

        assertThat(liveData.getValue().getRating()).isEqualTo(detailsResultMutableLiveData.getValue().getRating());
    }

    @Test
    public void callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectAddress() {
        // Given
        detailsResultMutableLiveData.setValue(detailsResult);
        // When
        when(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData);
        // Then
        LiveData<DetailsResult> liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key);

        assertThat(liveData.getValue().getAddress()).isEqualTo(detailsResultMutableLiveData.getValue().getAddress());
    }

    @Test
    public void callDetailsApi_DetailsRestaurantSuccess_ReturnCorrectWebsite() {
        // Given
        detailsResultMutableLiveData.setValue(detailsResult);
        // When
        when(restaurantViewModelTest.getDetailsRestaurant(placeId, key)).thenReturn(detailsResultMutableLiveData);
        // Then
        LiveData<DetailsResult> liveData = restaurantViewModelTest.getDetailsRestaurant(placeId, key);

        assertThat(liveData.getValue().getWebsite()).isEqualTo(detailsResultMutableLiveData.getValue().getWebsite());
    }

    @Test
    public void callDetailsApi_WithWrongKey_ReturnError() {
        when(restaurantViewModelTest.getDetailsRestaurant(placeId, "wrong key")).thenReturn(null);
        assertThat(restaurantViewModelTest.getDetailsRestaurant(placeId, "wrong key")).isNull();
    }
}