package com.example.weathercast.data.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weathercast.internal.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

@RequiresApi(Build.VERSION_CODES.M)
class ConnectivityInterceptorImpl(
    context: Context
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isInternetAvailable()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        val network = connectivityManager!!.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        if (networkCapabilities == null) {
            Log.d("Connection", "No network capabilities found")
            return false
        }
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}