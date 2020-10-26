package com.example.weathercast.ui.base

import androidx.lifecycle.ViewModel
import com.example.weathercast.data.repository.ForecastRepository
import com.example.weathercast.internal.lazyDeferred

abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}