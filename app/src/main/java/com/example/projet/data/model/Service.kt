package com.example.projet.data.model

import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val photoURL: String?,
    val providerId: String,
    val categoryId: String
)