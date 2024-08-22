package com.example.synamedia_assignment.di

import androidx.lifecycle.ViewModel
import com.example.synamedia_assignment.viewmodels.WeatherViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @ClassKey(WeatherViewModel::class)
    @IntoMap
    abstract fun provideMainViewModel(weatherViewModel: WeatherViewModel): ViewModel



}