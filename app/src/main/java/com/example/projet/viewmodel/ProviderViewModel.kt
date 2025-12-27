package com.example.projet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.Booking
import com.example.projet.data.model.Category
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.ProviderStats
import com.example.projet.data.model.Review
import com.example.projet.data.model.Service
import com.example.projet.data.repository.ProviderRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProviderViewModel(
    private val repository: ProviderRepository
) : ViewModel() {

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _stats = MutableLiveData<ProviderStats>()
    val stats: LiveData<ProviderStats> = _stats

    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

    private val _updateBookingDateSuccess = MutableLiveData<MessageResponse>()
    val updateBookingDateSuccess: LiveData<MessageResponse> = _updateBookingDateSuccess

    private val _updateBookingDateError = MutableLiveData<String>()
    val updateBookingDateError: LiveData<String> = _updateBookingDateError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadProviderServices(providerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getProviderServices(providerId)
                _services.value = result
                updateStats()
            } catch (e: Exception) {
                Log.e("ProviderViewModel", "Error loading services", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                Log.e("ProviderViewModel", "Error loading categories", e)
            }
        }
    }

    fun createService(
        title: RequestBody,
        desc: RequestBody,
        price: RequestBody,
        providerId: RequestBody,
        categoryId: RequestBody,
        photo: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createService(title, desc, price, providerId, categoryId, photo)
                _operationStatus.value = Result.success("Service Created Successfully")
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteService(serviceId: String, providerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteService(serviceId)
                _operationStatus.value = Result.success("Service Deleted")
                loadProviderServices(providerId)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateService(
        serviceId: String,
        title: RequestBody,
        desc: RequestBody,
        price: RequestBody,
        categoryId: RequestBody,
        photo: MultipartBody.Part?,
        providerId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateService(serviceId, title, desc, price, categoryId, photo)
                _operationStatus.value = Result.success("Service Updated")
                loadProviderServices(providerId)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBookings(providerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getBookingsByProvider(providerId)
                _bookings.value = result
                updateStats()
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptBooking(bookingId: String, providerId: String) {
        viewModelScope.launch {
            try {
                repository.acceptBooking(bookingId)
                _operationStatus.value = Result.success("Booking Accepted")
                loadBookings(providerId)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun rejectBooking(bookingId: String, providerId: String) {
        viewModelScope.launch {
            try {
                repository.rejectBooking(bookingId)
                _operationStatus.value = Result.success("Booking Rejected")
                loadBookings(providerId)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun loadReviews(providerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getReviewsByProvider(providerId)
                _reviews.value = result
                updateStats()
            } catch (e: Exception) {
                Log.e("ProviderViewModel", "Error loading reviews", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- FIXED FUNCTION: Update Booking Date with providerId included ---
    fun updateBookingDate(bookingId: String, date: String, providerId: String) {
        viewModelScope.launch {
            try {
                // Send both date and providerId in body to backend
                val body = mapOf(
                    "newDate" to date,
                    "providerId" to providerId
                )
                val response = repository.updateBookingDate(bookingId, body)
                _updateBookingDateSuccess.value = response
                loadBookings(providerId)
            } catch (e: Exception) {
                _updateBookingDateError.value = e.message
            }
        }
    }

    private fun updateStats() {
        val bookingsCount = _bookings.value?.size ?: 0
        val servicesCount = _services.value?.size ?: 0
        val reviewsCount = _reviews.value?.size ?: 0
        _stats.value = ProviderStats(bookingsCount, servicesCount, reviewsCount)
    }
}
