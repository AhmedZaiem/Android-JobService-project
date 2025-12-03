package com.example.projet.data.api

import com.example.projet.data.model.Booking
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.LoginResponse
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.Service
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ProviderApi {



    // SERVICES
    @GET("services/provider/{providerId}")
    suspend fun getProviderServices(@Path("providerId") providerId: String): List<Service>

    @Multipart
    @POST("services/create")
    suspend fun createService(
        @Part("title") title: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("providerId") providerId: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Service

    @DELETE("services/delete/{id}")
    suspend fun deleteService(@Path("id") id: String): MessageResponse

    // BOOKINGS
    @GET("services/bookings/{providerId}")
    suspend fun getBookings(@Path("providerId") providerId: String): List<Booking>

    @PUT("services/booking/accept/{id}")
    suspend fun acceptBooking(@Path("id") bookingId: String): MessageResponse

    @PUT("services/booking/reject/{id}")
    suspend fun rejectBooking(@Path("id") bookingId: String): MessageResponse

    @PUT("services/booking/complete/{id}")
    suspend fun completeBooking(
        @Path("id") bookingId: String,
        @Body providerId: Map<String, String>
    ): MessageResponse
}