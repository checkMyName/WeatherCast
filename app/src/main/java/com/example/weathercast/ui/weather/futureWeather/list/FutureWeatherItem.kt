package com.example.weathercast.ui.weather.futureWeather.list

import com.example.weathercast.R
import com.example.weathercast.data.db.unit.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.*
import kotlinx.android.synthetic.main.future_weather_item.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureWeatherItem(
    val weatherEntry: UnitSpecificSimpleFutureWeatherEntry
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            textView_condition.text = weatherEntry.conditionText
            updateDate()
            updateTemperature()
            updateConditionImage()
        }
    }

    override fun getLayout() = R.layout.future_weather_item

    private fun ViewHolder.updateDate() {
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        textView_date.text = weatherEntry.date.format(dtFormatter)
    }

    private fun ViewHolder.updateTemperature() {
        textView_temperature.text = "${weatherEntry.avgTemperature}°C"
    }

    private fun ViewHolder.updateConditionImage() {
        Picasso.get()
            .load("http:" + weatherEntry.conditionIconUrl)
            .into(imageView_condition_icon)
    }
}