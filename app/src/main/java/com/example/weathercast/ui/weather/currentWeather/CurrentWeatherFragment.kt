package com.example.weathercast.ui.weather.currentWeather

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weathercast.R
import com.example.weathercast.ui.base.ScopedFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class CurrentWeatherFragment : ScopedFragment(), DIAware {

    override val di by closestDI()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()

    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch {
        val currentWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })

        currentWeather.observe(viewLifecycleOwner, Observer {

            if (it == null) {
                return@Observer
            }

            group_loading.visibility = View.GONE
            updateDateToToday()
            updateTemperatures(it.temperature, it.feelsLikeTemperature)
            updateCondition(it.conditionText)
            updatePrecipitation(it.precipitationVolume)
            updateWind(it.windDirection, it.windSpeed)
            updateVisibility(it.visibilityDistance)

            Picasso.get()
                .load("http:${it.conditionIconUrl}")
                .into(imageView_condition_icon)
        })
    }

    private fun updateLocation(location: String) {
        (activity as AppCompatActivity).supportActionBar?.title = location
    }

    private fun updateDateToToday() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLikeTemp: Double) {
        textView_temperature.text = "$temperature°C"
        textView_feels_like_temperature.text = "Feels Like: $feelsLikeTemp°C"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        textView_precipitation.text = "Precipitation: $precipitationVolume mm"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        textView_wind.text = "Wind: $windDirection, $windSpeed kmh"
    }

    private fun updateVisibility(visibility: Double) {
        textView_visibility.text = "Visibility: $visibility km"
    }

}