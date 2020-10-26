package com.example.weathercast.data.db.entity

import com.example.weathercast.data.network.response.CurrentWeatherEntry
import com.google.gson.annotations.SerializedName


data class CurrentWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: WeatherLocation
)