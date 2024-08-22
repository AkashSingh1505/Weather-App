package com.example.synamedia_assignment.retrofit

import com.example.synamedia_assignment.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

}