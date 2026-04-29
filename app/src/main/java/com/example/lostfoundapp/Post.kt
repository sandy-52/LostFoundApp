package com.example.lostfoundapp

data class Post(
    val id: Int = 0,
    val type: String,
    val name: String,
    val phone: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String,
    val imageUri: String,
    val timestamp: String
)