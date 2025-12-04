package com.example.projet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projet.data.repository.ProviderRepository

class ProviderViewModelFactory(
    private val repository: ProviderRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderViewModel::class.java)) {
            return ProviderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}