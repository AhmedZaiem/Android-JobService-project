package com.example.projet.data.repository

import com.example.projet.data.api.ProviderApi
import com.example.projet.data.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProviderRepository {

    private val api = RetrofitClient.instance.create(ProviderApi::class.java)

    suspend fun getProviderServices(id: String) = api.getProviderServices(id)

    suspend fun createService(
        title: String,
        description: String,
        price: String,
        providerId: String,
        categoryId: String,
        imageFile: File?
    ): com.example.projet.data.model.Service {
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
        val providerIdPart = providerId.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryIdPart = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())

        val photoPart = imageFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", it.name, requestFile)
        }

        return api.createService(
            title = titlePart,
            desc = descriptionPart,
            price = pricePart,
            providerId = providerIdPart,
            categoryId = categoryIdPart,
            photo = photoPart
        )
    }

    suspend fun deleteService(id: String) = api.deleteService(id)

    suspend fun getBookings(id: String) = api.getBookings(id)

    suspend fun acceptBooking(id: String) = api.acceptBooking(id)

    suspend fun rejectBooking(id: String) = api.rejectBooking(id)

    suspend fun completeBooking(bookingId: String, providerId: String) =
        api.completeBooking(bookingId, mapOf("providerId" to providerId))
}
