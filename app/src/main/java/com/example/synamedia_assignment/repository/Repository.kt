package com.example.synamedia_assignment.repository

import com.example.synamedia_assignment.model.WeatherResponse
import com.example.synamedia_assignment.retrofit.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject



class Repository @Inject constructor(
    private val api: ApiService
) {
    private val _weatherResponse = MutableStateFlow<WeatherResponse?>(null)
    val weatherResponse: StateFlow<WeatherResponse?>
        get() = _weatherResponse
    suspend fun getCurrentWeather(location: String, apiKey: String){
        val result =api.getCurrentWeather(location, apiKey)
        if (result.isSuccessful && result.body() != null) {
            _weatherResponse.emit(result.body()!!)
        }
    }
}

