package com.example.projet.data.repository

import com.example.projet.data.api.CustomerApi
import com.example.projet.data.api.RetrofitClient


class CustomerRepository {

    private val api = RetrofitClient.instance.create(CustomerApi::class.java)

    suspend fun getAllServices() = api.getAllServices()
    suspend fun getCustomerBookings(id: String) = api.getCustomerBookings(id)

}
