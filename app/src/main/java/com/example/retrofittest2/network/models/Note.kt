package com.example.retrofittest2.network.models

import com.google.gson.annotations.SerializedName

data class Note(
    var content: String,
    val created_at: String?,
    val id: Int?,
    @SerializedName("ownerId")
    val ownerId: Int,
    var title: String,
    val updated_at: String?
)