package com.example.projet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Booking(
    val _id: String,
    val status: String,
    val customerId: Customer,
    val serviceId: Service
)

data class Customer(
    val name: String,
    val email: String,
    val tel: String,
    val city: String
)
