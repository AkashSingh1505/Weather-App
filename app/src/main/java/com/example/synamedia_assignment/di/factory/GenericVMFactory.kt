package com.example.synamedia_assignment.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class GenericVMFactory @Inject constructor(private val map: Map<Class<*>,@JvmSuppressWildcards ViewModel>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return map.get(modelClass) as T
    }
}