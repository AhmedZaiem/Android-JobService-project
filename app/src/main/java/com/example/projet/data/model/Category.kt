package com.example.projet.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("_id") val id: String,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}