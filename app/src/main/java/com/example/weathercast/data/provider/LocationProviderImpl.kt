package com.example.weathercast.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.weathercast.data.db.entity.WeatherLocation
import com.example.weathercast.internal.LocationPermissionNotGrantedException
import com.example.weathercast.internal.asDeferred
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

    private val appContext: Context = context.applicationContext

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = hasDeviceLocationChanged(lastWeatherLocation)

        return deviceLocationChanged || try {
            hasCustomLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
    }

    override suspend fun getPreferredLocationString(): String {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation()
                    ?: return "${getCustomLocationName()}"
                return "${deviceLocation.latitude}, ${deviceLocation.longitude}"
            } catch (e: LocationPermissionNotGrantedException) {
                return "${getCustomLocationName()}"
            }
        } else {
            return "${getCustomLocationName()}"
        }
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation()) {
            return false
        }

        val deviceLocation = getLastDeviceLocation()
            ?: return false

        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold
                && Math.abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            return customLocationName != lastWeatherLocation.name
        }
        return false
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastDeviceLocation(): Location? {
        return (if (hasLocationPermission()) {
            fusedLocationProviderClient.lastLocation.asDeferred()
        } else {
            throw LocationPermissionNotGrantedException()
        }).await()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}