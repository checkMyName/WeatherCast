package com.example.weathercast.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weathercast.data.WeatherAPIService
import com.example.weathercast.data.db.entity.CurrentWeatherResponse
import com.example.weathercast.data.network.response.FutureWeatherResponse
import com.example.weathercast.internal.NoConnectivityException

const val FORECAST_DAYS_COUNT = 7

class WeatherNetworkDataSourceImpl(
    private val apiService: WeatherAPIService
) : WeatherNetworkDataSource {

    private var _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val fetchCurrentWeather = apiService
                .getCurrentWeatherAsync(location)
                .await()
            _downloadedCurrentWeather.postValue(fetchCurrentWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connection", "No Internet connection!", e)
        }
    }

    private var _downloadedFutureWeather = MutableLiveData<FutureWeatherResponse>()
    override val downloadedFutureWeather: LiveData<FutureWeatherResponse>
        get() = _downloadedFutureWeather

    override suspend fun fetchFutureWeather(location: String) {
        try {
            val fetchFutureWeather = apiService
                .getFutureWeatherAsync(location, FORECAST_DAYS_COUNT)
                .await()
            _downloadedFutureWeather.postValue(fetchFutureWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connection", "No Internet connection!", e)
        }
    }
}