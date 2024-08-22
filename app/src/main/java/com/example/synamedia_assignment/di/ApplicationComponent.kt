package com.example.synamedia_assignment.ApplicationComponent

import androidx.lifecycle.ViewModel
import com.example.synamedia_assignment.di.NetworkModule
import com.example.synamedia_assignment.di.ViewModelModule
import com.example.synamedia_assignment.ui.MainActivity
import dagger.Component
import javax.inject.Singleton



@Singleton
@Component(modules = [ViewModelModule::class, NetworkModule::class])
interface ApplicationComponent {

    fun injectMainActivity(target: MainActivity)
    fun getMap():Map<Class<*>, ViewModel>

}
