package com.example.weathercast.ui.weather.futureWeather.list

import androidx.lifecycle.ViewModel
import com.example.weathercast.data.repository.ForecastRepository
import com.example.weathercast.internal.lazyDeferred
import com.example.weathercast.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : WeatherViewModel(forecastRepository) {

    val weatherEntries by lazyDeferred {
        forecastRepository.getFutureWeatherList(LocalDate.now())
    }

}