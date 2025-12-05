package com.example.projet.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // IMPORTANT: When using a physical device via USB, you have two options:
    // 1. Run 'adb reverse tcp:5000 tcp:5000' in your terminal, then use "http://127.0.0.1:5000/api/"
    // 2. OR find your PC's local IP address (e.g., ipconfig on Windows) and use that, e.g., "http://192.168.1.100:5000/api/"
    
    // Currently set to a common local IP placeholder. UPDATE THIS to your actual PC IP if not using adb reverse.
    private const val BASE_URL = "http://192.168.1.100:5000/api/"

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
