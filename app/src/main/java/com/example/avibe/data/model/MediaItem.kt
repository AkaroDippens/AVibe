package com.example.avibe.data.model

// Data class for media item
data class MediaItem(
    val id: String,
    val name: String,
    val type: MediaType,
    val uri: String,
    val dateAdded: Long
)