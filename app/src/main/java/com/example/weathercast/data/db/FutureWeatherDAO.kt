package com.example.weathercast.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathercast.data.db.entity.FutureWeatherEntry
import com.example.weathercast.data.db.unit.future.detail.MetricDetailFutureWeatherEntry
import com.example.weathercast.data.db.unit.future.list.MetricSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate

@Dao
interface FutureWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<FutureWeatherEntry>)

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastMetric(startDate: LocalDate): LiveData<List<MetricSimpleFutureWeatherEntry>>

    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailWeatherByDateMetric(date: LocalDate): LiveData<MetricDetailFutureWeatherEntry>

    @Query("select count(id) from future_weather where date(date) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from future_weather where date(date) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)
}