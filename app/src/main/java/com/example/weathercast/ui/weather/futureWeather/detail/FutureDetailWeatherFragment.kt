package com.example.weathercast.ui.weather.futureWeather.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weathercast.R
import com.example.weathercast.data.db.LocalDateConverter
import com.example.weathercast.internal.DateNotFoundException
import com.example.weathercast.ui.base.ScopedFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.future_detail_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.factory
import org.kodein.di.instance
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureDetailWeatherFragment : ScopedFragment(), DIAware {

    override val di by closestDI()

    private val viewModelFactoryInstanceFactory : ((LocalDate) -> FutureDetailWeatherViewModelFactory) by factory()
    private lateinit var viewModel: FutureDetailWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_detail_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val safeArgs = arguments?.let {
            FutureDetailWeatherFragmentArgs.fromBundle(it)
        }

        val date = LocalDateConverter.stringToDate(safeArgs?.dateString) ?: throw DateNotFoundException()

        viewModel = ViewModelProvider(this, viewModelFactoryInstanceFactory(date)).get(FutureDetailWeatherViewModel::class.java)


        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val futureWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })

        futureWeather.observe(viewLifecycleOwner, Observer { weatherEntry ->
            if (weatherEntry == null) return@Observer

            updateDate(weatherEntry.date)
            updateTemperatures(weatherEntry.avgTemperature,
                weatherEntry.minTemperature, weatherEntry.maxTemperature)
            updateCondition(weatherEntry.conditionText)
            updatePrecipitation(weatherEntry.totalPrecipitation)
            updateWindSpeed(weatherEntry.maxWindSpeed)
            updateVisibility(weatherEntry.avgVisibilityDistance)
            updateUv(weatherEntry.uv)

            Picasso.get()
                .load("http:" + weatherEntry.conditionIconUrl)
                .into(imageView_condition_icon)
        })
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDate(date: LocalDate) {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    private fun updateTemperatures(temperature: Double, min: Double, max: Double) {
        textView_temperature.text = "$temperature°C"
        textView_min_max_temperature.text = "Min: $min°C, Max: $max°C"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        textView_precipitation.text = "Precipitation: $precipitationVolume mm"
    }

    private fun updateWindSpeed(windSpeed: Double) {
        textView_wind.text = "Wind speed: $windSpeed kph"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        textView_visibility.text = "Visibility: $visibilityDistance km"
    }

    private fun updateUv(uv: Double) {
        textView_uv.text = "UV: $uv"
    }

}