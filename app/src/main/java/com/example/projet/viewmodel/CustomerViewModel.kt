package com.example.projet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.Booking
import com.example.projet.data.model.BookServiceRequest
import com.example.projet.data.model.Category
import com.example.projet.data.model.ReviewRequest
import com.example.projet.data.model.Service
import com.example.projet.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    private var allServices: List<Service> = emptyList()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<Category?>(null)

    val filteredServices: StateFlow<List<Service>> = 
        combine(searchQuery, selectedCategory, _services) { query, category, services ->
            val filteredList = if (query.isBlank()) services else services.filter { 
                it.title.contains(query, ignoreCase = true) 
            }
            if (category == null || category.name == "All categories") {
                filteredList
            } else {
                filteredList.filter { it.categoryId == category.id }
            }
        }.let { flow ->
            val stateFlow = MutableStateFlow<List<Service>>(emptyList())
            viewModelScope.launch {
                flow.collect { stateFlow.value = it }
            }
            stateFlow.asStateFlow()
        }

    init {
        loadAllServices()
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val categoryList = repository.getCategories()
                _categories.value = listOf(Category("", "All categories")) + categoryList
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error loading categories", e)
            }
        }
    }

    fun loadAllServices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                allServices = repository.getAllServices()
                _services.value = allServices
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error loading services", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCustomerBookings(customerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _bookings.value = repository.getCustomerBookings(customerId)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error loading bookings", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun bookService(request: BookServiceRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.bookService(request)
                _operationStatus.value = Result.success("Service booked successfully")
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error booking service", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelBooking(bookingId: String, customerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.cancelBooking(bookingId)
                _operationStatus.value = Result.success("Booking cancelled successfully")
                loadCustomerBookings(customerId)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error cancelling booking", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitReview(review: ReviewRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createReview(review)
                _operationStatus.value = Result.success("Review submitted successfully")
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error submitting review", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}