package com.example.weathercast.data.network

import androidx.lifecycle.LiveData
import com.example.weathercast.data.db.entity.CurrentWeatherResponse
import com.example.weathercast.data.network.response.FutureWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedFutureWeather: LiveData<FutureWeatherResponse>

    suspend fun fetchCurrentWeather(
        location: String
    )

    suspend fun fetchFutureWeather(
        location: String
    )
}