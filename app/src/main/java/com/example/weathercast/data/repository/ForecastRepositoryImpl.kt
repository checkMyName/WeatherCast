package com.example.weathercast.data.repository

import androidx.lifecycle.LiveData
import com.example.weathercast.data.db.CurrentWeatherDAO
import com.example.weathercast.data.db.FutureWeatherDAO
import com.example.weathercast.data.db.WeatherLocationDAO
import com.example.weathercast.data.db.entity.CurrentWeatherResponse
import com.example.weathercast.data.db.entity.WeatherLocation
import com.example.weathercast.data.db.unit.current.UnitSpecificCurrentWeatherEntry
import com.example.weathercast.data.db.unit.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.example.weathercast.data.db.unit.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.example.weathercast.data.network.FORECAST_DAYS_COUNT
import com.example.weathercast.data.network.WeatherNetworkDataSource
import com.example.weathercast.data.network.response.FutureWeatherResponse
import com.example.weathercast.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(
    private val currentWeatherDAO: CurrentWeatherDAO,
    private val futureWeatherDAO: FutureWeatherDAO,
    private val weatherLocationDAO: WeatherLocationDAO,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }

            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }

    override suspend fun getCurrentWeather(): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDAO.getWeatherMetric()
        }
    }

    override suspend fun getFutureWeatherList(startDate: LocalDate): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext futureWeatherDAO.getSimpleWeatherForecastMetric(startDate)
        }
    }

    override suspend fun getFutureWeatherByDate(date: LocalDate): LiveData<out UnitSpecificDetailFutureWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext futureWeatherDAO.getDetailWeatherByDateMetric(date)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDAO.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDAO.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDAO.upsert(fetchedWeather.location)
        }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {

        fun deleteOldForecastData() {
            val today = LocalDate.now()
            futureWeatherDAO.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
            futureWeatherDAO.insert(futureWeatherList)
            weatherLocationDAO.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDAO.getLocationNonLive()

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)) {
            fetchCurrentWeather()
        }

        if (isFetchFutureNeeded()) {
            fetchFutureWeather()
        }
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString())
    }

    private suspend fun fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(locationProvider.getPreferredLocationString())
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDAO.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }
}