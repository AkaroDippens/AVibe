package com.example.avibe.data.model

import kotlinx.serialization.Serializable

// Serializable data class for media item (used in JSON)
@Serializable
data class SerializableMediaItem(
    val id: String,
    val name: String,
    val type: String,
    val uri: String,
    val dateAdded: Long
)