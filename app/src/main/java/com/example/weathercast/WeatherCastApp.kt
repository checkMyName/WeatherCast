package com.example.weathercast

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import com.example.weathercast.data.WeatherAPIService
import com.example.weathercast.data.db.ForecastDatabase
import com.example.weathercast.data.network.ConnectivityInterceptor
import com.example.weathercast.data.network.ConnectivityInterceptorImpl
import com.example.weathercast.data.network.WeatherNetworkDataSource
import com.example.weathercast.data.network.WeatherNetworkDataSourceImpl
import com.example.weathercast.data.provider.LocationProvider
import com.example.weathercast.data.provider.LocationProviderImpl
import com.example.weathercast.data.repository.ForecastRepository
import com.example.weathercast.data.repository.ForecastRepositoryImpl
import com.example.weathercast.ui.weather.currentWeather.CurrentWeatherViewModelFactory
import com.example.weathercast.ui.weather.futureWeather.detail.FutureDetailWeatherViewModelFactory
import com.example.weathercast.ui.weather.futureWeather.list.FutureListWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule
import org.threeten.bp.LocalDate

class WeatherCastApp : Application(), DIAware {
    @RequiresApi(Build.VERSION_CODES.M)
    override val di = DI.lazy {
        import(androidXModule(this@WeatherCastApp))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDAO() }
        bind() from singleton { instance<ForecastDatabase>().futureWeatherDAO() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDAO() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { WeatherAPIService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(), instance(), instance(), instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance()) }
        bind() from provider { FutureListWeatherViewModelFactory(instance()) }
        bind() from factory { detailDate: LocalDate -> FutureDetailWeatherViewModelFactory(detailDate, instance())}
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

}
