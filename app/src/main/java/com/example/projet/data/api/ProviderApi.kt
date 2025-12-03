package com.example.projet.data.api

import com.example.projet.data.model.Booking
import com.example.projet.data.model.Category
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.Service
import com.example.projet.data.model.Review
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ProviderApi {

    // SERVICES
    @Multipart
    @POST("provider/service")
    suspend fun createService(
        @Part("title") title: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("providerId") providerId: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Service

    @DELETE("provider/service/{serviceId}")
    suspend fun deleteService(@Path("serviceId") serviceId: String): MessageResponse

    @Multipart
    @PUT("provider/service/{serviceId}")
    suspend fun updateService(
        @Path("serviceId") serviceId: String,
        @Part("title") title: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Service

    @GET("provider/services/provider/{providerId}")
    suspend fun getProviderServices(@Path("providerId") providerId: String): List<Service>

    @GET("provider/service/{serviceId}")
    suspend fun getServiceById(@Path("serviceId") serviceId: String): Service

    // BOOKINGS
    @GET("provider/bookings/provider/{providerId}")
    suspend fun getBookingsByProvider(@Path("providerId") providerId: String): List<Booking>

    @PUT("provider/booking/accept/{bookingId}")
    suspend fun acceptBooking(@Path("bookingId") bookingId: String): MessageResponse

    @PUT("provider/booking/reject/{bookingId}")
    suspend fun rejectBooking(@Path("bookingId") bookingId: String): MessageResponse

    // REVIEWS
    @GET("provider/reviews/{providerId}")
    suspend fun getReviewsByProvider(@Path("providerId") providerId: String): List<Review>

    // CATEGORIES
    @GET("admin/categories")
    suspend fun getCategories(): List<Category>
}
