package com.example.projet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.Booking
import com.example.projet.data.model.BookServiceRequest
import com.example.projet.data.model.ReviewRequest
import com.example.projet.data.model.Service
import com.example.projet.data.repository.CustomerRepository
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

    fun loadAllServices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllServices()
                _services.value = result
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
                val result = repository.getCustomerBookings(customerId)
                _bookings.value = result
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
                // Refresh bookings list
                loadCustomerBookings(customerId)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error cancelling booking", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitReview(serviceId: String, review: ReviewRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createReview(serviceId, review)
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
