package com.example.pokemon_app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.synamedia_assignment.BuildConfig

object Constants {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val ACCEPT_HEADER = "application/json"
    const val TAG = "Pokemon"
    const val PERMISSION_REQUEST_CODE = 123
    const val  apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY



}