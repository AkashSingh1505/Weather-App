package com.example.synamedia_assignment.di

import com.example.pokemon_app.utils.Constants
import com.example.synamedia_assignment.retrofit.ApiService

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit():Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

    }

    @Singleton
    @Provides
    fun providesApiService(retrofitBuilder: Retrofit.Builder): ApiService {
        val apiClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)

        return retrofitBuilder
            .client(apiClient.build())
            .build().create(ApiService::class.java);
    }

}