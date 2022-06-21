package com.example.dsgcodingchallenge

data class Event (
    val id: String,
    val title: String,
    val location: String,
    val dateandtime: String,
    val image_url: String,
    var isFavorite: Boolean = false
)