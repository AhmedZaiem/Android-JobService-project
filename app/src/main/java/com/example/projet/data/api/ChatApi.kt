package com.example.projet.data.api

import com.example.projet.data.model.Chat
import com.example.projet.data.model.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {

    @GET("chat/{userId}/{otherUserId}")
    suspend fun getChatHistory(
        @Path("userId") userId: String,
        @Path("otherUserId") otherUserId: String
    ):MessageResponse
}
