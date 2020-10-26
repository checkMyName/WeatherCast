package com.example.weathercast.data.network.response


import com.example.weathercast.data.db.entity.FutureWeatherEntry
import com.google.gson.annotations.SerializedName

data class ForecastDaysContainer(
@SerializedName("forecastday")
    val entries: List<FutureWeatherEntry>
)