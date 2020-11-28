package com.gallosalocin.go4lunch.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gallosalocin.go4lunch.models.RestaurantResult;
import com.gallosalocin.go4lunch.services.RestaurantApi;
import com.gallosalocin.go4lunch.services.dto.DetailsResponse;
import com.gallosalocin.go4lunch.services.dto.DetailsResult;
import com.gallosalocin.go4lunch.services.dto.RestaurantResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.gallosalocin.go4lunch.util.Constants.BASE_URL;

public class RestaurantViewModel extends AndroidViewModel {

    private RestaurantApi restaurantApi;
    private MutableLiveData<List<RestaurantResult>> restaurantList;
    private MutableLiveData<DetailsResult> detailsResult;

    public RestaurantViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<RestaurantResult>> getRestaurants(String currentLocation, int radius, String type, String key) {
        if (restaurantList == null) {
            restaurantList = new MutableLiveData<>();
            loadRestaurants(currentLocation, radius, type, key);
        }
        return restaurantList;
    }

    public LiveData<DetailsResult> getDetailsRestaurant(String placeId, String key) {
        detailsResult = new MutableLiveData<>();
        loadDetailsRestaurant(placeId, key);
        return detailsResult;
    }

    private void setupRestaurantApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        restaurantApi = retrofit.create(RestaurantApi.class);
    }

    private void loadRestaurants(String currentLocation, int radius, String type, String key) {
        setupRestaurantApi();

        Call<RestaurantResponse> restaurantResponseCall = restaurantApi.getNearbyRestaurant(currentLocation, radius, type, key);
        restaurantResponseCall.enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(@NotNull Call<RestaurantResponse> call, @NotNull Response<RestaurantResponse> response) {
                if (response.body() != null) {
                    restaurantList.setValue(response.body().getRestaurantResults());
                }
            }

            @Override
            public void onFailure(@NotNull Call<RestaurantResponse> call, @NotNull Throwable t) {
            }
        });
    }

    private void loadDetailsRestaurant(String placeId, String key) {
        setupRestaurantApi();

        Call<DetailsResponse> detailsResponseCall = restaurantApi.getDetailsRestaurant(placeId, key);
        detailsResponseCall.enqueue(new Callback<DetailsResponse>() {
            @Override
            public void onResponse(@NotNull Call<DetailsResponse> call, @NotNull Response<DetailsResponse> response) {
                if (response.body() != null) {
                    detailsResult.setValue(response.body().getDetailsResult());
                }
            }

            @Override
            public void onFailure(@NotNull Call<DetailsResponse> call, @NotNull Throwable t) {
                Timber.e("Error loadDetailsRestaurant : %s", t.getMessage());
            }
        });

    }

}
