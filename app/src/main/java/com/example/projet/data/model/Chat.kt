package com.example.projet.data.model

import java.util.Date

data class Chat(
    val id: String? = null,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Date = Date()
)
