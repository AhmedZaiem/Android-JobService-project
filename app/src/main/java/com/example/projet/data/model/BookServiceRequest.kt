package com.example.projet.data.model

data class BookServiceRequest(
    val serviceId: String,
    val customerId: String,
    val date: String,
    val time: String,
    val address: String
)
