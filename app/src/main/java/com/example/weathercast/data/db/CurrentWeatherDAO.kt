package com.example.weathercast.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathercast.data.db.unit.current.MetricCurrentWeatherEntry
import com.example.weathercast.data.network.response.CURRENT_WEATHER_ID
import com.example.weathercast.data.network.response.CurrentWeatherEntry

@Dao
interface CurrentWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherEntry: CurrentWeatherEntry)

    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
    fun getWeatherMetric(): LiveData<MetricCurrentWeatherEntry>
}