package com.example.avibe.data.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("media_preferences")

class MediaRepository(private val context: Context) {
    
    private val json = Json { ignoreUnknownKeys = true }
    private val dataStore = context.dataStore

    companion object {
        private val MEDIA_LIST_KEY = stringPreferencesKey("media_list")
        private val THEME_KEY = booleanPreferencesKey("theme")
    }

    /**
     * Saving media list to JSON
     * @param mediaList list of media items.
     */
    suspend fun saveMediaList(mediaList: List<MediaItem>) {
        val serializableList = mediaList.map { item ->
            SerializableMediaItem(
                id = item.id,
                name = item.name,
                type = item.type.name,
                uri = item.uri,
                dateAdded = item.dateAdded
            )
        }
        val jsonString = json.encodeToString(serializableList)
        dataStore.edit { preferences ->
            preferences[MEDIA_LIST_KEY] = jsonString
        }
    }

    // Getting media list from JSON
    fun getMediaList(): Flow<List<MediaItem>> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[MEDIA_LIST_KEY] ?: return@map emptyList()
            try {
                val serializableList = json.decodeFromString<List<SerializableMediaItem>>(jsonString)
                serializableList.map { item ->
                    MediaItem(
                        id = item.id,
                        name = item.name,
                        type = MediaType.valueOf(item.type),
                        uri = item.uri,
                        dateAdded = item.dateAdded
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Deleting media item from list
     * @param itemId id of media item
     */
    suspend fun deleteMediaItem(itemId: String) {
        val currentList = getMediaList().first()
        val updatedList = currentList.filter { it.id != itemId }
        saveMediaList(updatedList)
    }

    /**
     * Renaming media item
     * @param itemId id of media item
     * @param newName new name
     */
    suspend fun renameMediaItem(itemId: String, newName: String) {
        val currentList = getMediaList().first()
        val updatedList = currentList.map { item ->
            if (item.id == itemId) {
                item.copy(name = newName)
            } else {
                item
            }
        }
        saveMediaList(updatedList)
    }

    /**
     * Saving theme
     * @param isDark is dark theme
     */
    suspend fun saveTheme(isDark: Boolean) {
        dataStore.edit { it[THEME_KEY] = isDark }
    }

    // Getting theme
    fun getTheme(): Flow<Boolean> {
        return dataStore.data.map { it[THEME_KEY] ?: true }
    }
}