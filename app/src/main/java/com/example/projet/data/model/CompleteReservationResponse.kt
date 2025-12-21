package com.example.projet.data.model

import com.google.gson.annotations.SerializedName

data class CompleteReservationResponse(
    val message: String,
    @SerializedName("reservation") val booking: Booking
)
