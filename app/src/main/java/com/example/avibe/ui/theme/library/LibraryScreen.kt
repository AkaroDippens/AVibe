package com.example.avibe.ui.theme.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.avibe.data.model.MediaItem

// Library screen for managing media
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    onAddClick: () -> Unit,
    isPlayerVisible: Boolean,
    onItemDelete: (MediaItem) -> Unit = {},    // Колбэк удаления
    onItemRename: (MediaItem, String) -> Unit = {_, _ ->} // Колбэк переименования
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AVibe") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.padding(bottom = if (isPlayerVisible) 72.dp else 0.dp)
            ) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = if (isPlayerVisible) 64.dp else 0.dp)
        ) {
            items(items) { item ->
                MediaItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    onDelete = { onItemDelete(item) }, // ✅ Вызываем колбэк
                    onRename = { newName -> onItemRename(item, newName) } // ✅ Вызываем колбэк
                )
            }
        }
    }
}