package com.example.weathercast.data.repository

import androidx.lifecycle.LiveData
import com.example.weathercast.data.db.entity.WeatherLocation
import com.example.weathercast.data.db.unit.current.UnitSpecificCurrentWeatherEntry
import com.example.weathercast.data.db.unit.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.example.weathercast.data.db.unit.future.list.UnitSpecificSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate

interface ForecastRepository {

    suspend fun getCurrentWeather(): LiveData<out UnitSpecificCurrentWeatherEntry>

    suspend fun getFutureWeatherList(startDate: LocalDate): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>>

    suspend fun getFutureWeatherByDate(date: LocalDate): LiveData<out UnitSpecificDetailFutureWeatherEntry>

    suspend fun getWeatherLocation(): LiveData<WeatherLocation>

}