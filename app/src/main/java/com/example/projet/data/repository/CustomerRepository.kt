package com.example.projet.data.repository

import com.example.projet.data.api.CustomerApi
import com.example.projet.data.api.RetrofitClient
import com.example.projet.data.model.BookServiceRequest
import com.example.projet.data.model.ReviewRequest


class CustomerRepository {

    private val api = RetrofitClient.instance.create(CustomerApi::class.java)

    suspend fun getAllServices() = api.getAllServices()
    suspend fun getCustomerBookings(id: String) = api.getCustomerBookings(id)
    suspend fun bookService(bookingData: BookServiceRequest) = api.bookService(bookingData)
    suspend fun cancelBooking(bookingId: String) = api.cancelBooking(bookingId)
    suspend fun createReview(serviceId: String, review: ReviewRequest) = api.createReview(serviceId, review)

}
