package com.example.projet.data.repository

import com.example.projet.data.api.ProviderApi
import com.example.projet.data.api.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProviderRepository {

    private val providerApi = RetrofitClient.instance.create(ProviderApi::class.java)

    suspend fun getProviderServices(providerId: String) = providerApi.getProviderServices(providerId)

    suspend fun createService(
        title: RequestBody,
        desc: RequestBody,
        price: RequestBody,
        providerId: RequestBody,
        categoryId: RequestBody,
        photo: MultipartBody.Part?
    ) = providerApi.createService(title, desc, price, providerId, categoryId, photo)

    suspend fun deleteService(serviceId: String) = providerApi.deleteService(serviceId)

    suspend fun updateService(
        serviceId: String,
        title: RequestBody,
        desc: RequestBody,
        price: RequestBody,
        categoryId: RequestBody,
        photo: MultipartBody.Part?
    ) = providerApi.updateService(serviceId, title, desc, price, categoryId, photo)

    suspend fun getBookingsByProvider(providerId: String) = providerApi.getBookingsByProvider(providerId)

    suspend fun acceptBooking(bookingId: String) = providerApi.acceptBooking(bookingId)

    suspend fun rejectBooking(bookingId: String) = providerApi.rejectBooking(bookingId)

    suspend fun getReviewsByProvider(providerId: String) = providerApi.getReviewsByProvider(providerId)

    suspend fun getCategories() = providerApi.getCategories()
}
