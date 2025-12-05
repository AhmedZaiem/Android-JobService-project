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
    @POST("services/book/{serviceId}")
    suspend fun bookService(
        @Path("serviceId") serviceId: String,
        @Body bookingData: BookServiceRequest
    ): Booking

    @GET("services/bookings/customer/{customerId}")
    suspend fun getCustomerBookings(
        @Path("customerId") customerId: String
    ): List<Booking>

    @PUT("services/booking/cancel/{bookingId}")
    suspend fun cancelBooking(
        @Path("bookingId") bookingId: String
    ): MessageResponse


    // ------------ REVIEWS ------------
    @POST("services/reviews/{serviceId}")
    suspend fun createReview(
        @Path("serviceId") serviceId: String,
        @Body review: ReviewRequest
    ): MessageResponse

}