package com.example.weathercast.ui.weather.futureWeather.detail

import com.example.weathercast.data.repository.ForecastRepository
import com.example.weathercast.internal.lazyDeferred
import com.example.weathercast.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureDetailWeatherViewModel(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository
) : WeatherViewModel(forecastRepository) {

    val weather by lazyDeferred {
        forecastRepository.getFutureWeatherByDate(detailDate)
    }

}