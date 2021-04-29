package com.gallosalocin.go4lunch.di

import com.gallosalocin.go4lunch.data.network.RestaurantApi
import com.gallosalocin.go4lunch.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun provideHttpClient(
            loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
            OkHttpClient.Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build()

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
            GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofitInstance(
            okHttpClient: OkHttpClient,
            gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(gsonConverterFactory)
                    .build()

    @Singleton
    @Provides
    fun provideRestaurantApi(retrofit: Retrofit): RestaurantApi =
            retrofit.create(RestaurantApi::class.java)

}