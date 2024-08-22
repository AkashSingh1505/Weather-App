package com.example.synamedia_assignment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synamedia_assignment.model.WeatherResponse
import com.example.synamedia_assignment.repository.Repository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherViewModel@Inject constructor(private val repository: Repository):ViewModel() {
    val weatherResponse: StateFlow<WeatherResponse?>
        get() = repository.weatherResponse

    fun getCurrentWeather(location: String, apiKey: String){
        viewModelScope.launch {
            repository.getCurrentWeather(location,apiKey)
        }
    }
}
