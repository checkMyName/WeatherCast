package com.example.weathercast.data

import com.example.weathercast.data.db.entity.CurrentWeatherResponse
import com.example.weathercast.data.network.ConnectivityInterceptor
import com.example.weathercast.data.network.ConnectivityInterceptorImpl
import com.example.weathercast.data.network.response.FutureWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "eee8a9b4e425497c9a4143842202909"

//http://api.weatherapi.com/v1/current.json?key=eee8a9b4e425497c9a4143842202909&q=London

//http://api.weatherapi.com/v1/forecast.json?key=eee8a9b4e425497c9a4143842202909&q=Astrakhan&days=1

interface WeatherAPIService {

    @GET("current.json")
    fun getCurrentWeatherAsync(
        @Query("q") location: String,
    ): Deferred<CurrentWeatherResponse>

    @GET("forecast.json")
    fun getFutureWeatherAsync(
        @Query("q") location: String,
        @Query("days") days: Int,
    ): Deferred<FutureWeatherResponse>


    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): WeatherAPIService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient
                .Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()


            return Retrofit.Builder().client(okHttpClient).baseUrl("http://api.weatherapi.com/v1/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(WeatherAPIService::class.java)
        }
    }
}