package com.example.projet.data.api


import com.example.projet.data.model.BookServiceRequest
import com.example.projet.data.model.Booking
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.ReviewRequest
import com.example.projet.data.model.Service
import retrofit2.http.*

interface CustomerApi {

    // ------------ SERVICES ------------
    @GET("admin/services")
    suspend fun getAllServices(): List<Service>


    // ------------ BOOKINGS ------------
    @POST("customer/book")
    suspend fun bookService(
        @Body bookingData: BookServiceRequest
    ): Booking

    @GET("customer/bookings/{customerId}")
    suspend fun getCustomerBookings(
        @Path("customerId") customerId: String
    ): List<Booking>

    @DELETE("customer/cancel/{bookingId}")
    suspend fun cancelBooking(
        @Path("bookingId") bookingId: String
    ): MessageResponse


    // ------------ REVIEWS ------------
    @POST("customer/review")
    suspend fun createReview(
        @Body review: ReviewRequest // include serviceId inside review if needed
    ): MessageResponse


}