package com.example.weathercast.ui.weather.currentWeather

import androidx.lifecycle.ViewModel
import com.example.weathercast.data.repository.ForecastRepository
import com.example.weathercast.internal.lazyDeferred
import com.example.weathercast.ui.base.WeatherViewModel

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : WeatherViewModel(forecastRepository) {

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather()
    }
}