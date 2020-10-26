package com.example.weathercast.data.network.response

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weathercast.data.db.entity.Condition
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeatherEntry(
    @Embedded(prefix = "condition_")
    val condition: Condition,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("gust_kph")
    val gustKph: Double,
    @SerializedName("is_day")
    val isDay: Int,
    @SerializedName("precip_in")
    val precipIn: Double,
    @SerializedName("precip_mm")
    val precipMm: Double,
    @SerializedName("pressure_in")
    val pressureIn: Double,
    @SerializedName("pressure_mb")
    val pressureMb: Double,
    @SerializedName("temp_c")
    val tempC: Double,
    val uv: Double,
    @SerializedName("vis_km")
    val visKm: Double,
    @SerializedName("wind_dir")
    val windDir: String,
    @SerializedName("wind_kph")
    val windKph: Double,
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID
}