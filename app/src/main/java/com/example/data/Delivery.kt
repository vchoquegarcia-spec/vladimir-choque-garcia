package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deliveries")
data class Delivery(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val phone: String,
    val pin: String,
    val quantity: Int,
    val deliveryDate: String, // Formato "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
)
