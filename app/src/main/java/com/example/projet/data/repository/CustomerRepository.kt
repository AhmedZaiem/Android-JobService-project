package com.example.projet.data.repository

import com.example.projet.data.api.AuthApi
import com.example.projet.data.api.CustomerApi
import com.example.projet.data.api.RetrofitClient
import com.example.projet.data.model.BookServiceRequest
import com.example.projet.data.model.ReviewRequest

class CustomerRepository {

    private val api = RetrofitClient.instance.create(CustomerApi::class.java)
    private val authApi = RetrofitClient.instance.create(AuthApi::class.java)

    suspend fun getAllServices() = api.getAllServices()
    suspend fun getCustomerBookings(id: String) = api.getCustomerBookings(id)
    suspend fun bookService(bookingData: BookServiceRequest) = api.bookService(bookingData)
    suspend fun cancelBooking(bookingId: String) = api.cancelBooking(bookingId)
    suspend fun createReview(review: ReviewRequest) = api.createReview(review)
    suspend fun getCategories() = authApi.getCategories()
    suspend fun completeReservation(reservationId: String, customerId: String) = api.completeReservation(reservationId, mapOf("customerId" to customerId))
}
