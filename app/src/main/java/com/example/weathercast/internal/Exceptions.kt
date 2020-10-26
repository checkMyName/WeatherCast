package com.example.weathercast.internal

import java.io.IOException
import java.lang.Exception

class NoConnectivityException: IOException()
class LocationPermissionNotGrantedException: Exception()
class DateNotFoundException: Exception()