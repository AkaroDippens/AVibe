package com.example.avibe.ui.theme.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avibe.data.model.MediaItem
import com.example.avibe.ui.theme.NeonPurple

// Library screen for managing media
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    onAddClick: () -> Unit,
    isPlayerVisible: Boolean,
    onItemDelete: (MediaItem) -> Unit = {},
    onItemRename: (MediaItem, String) -> Unit = { _, _ -> },
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    // Сhoosing a color depending on the theme
    val bgColor = if (isDarkTheme) Color(0xFF0A0E27) else MaterialTheme.colorScheme.background
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1F3A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground
    val fabColor = if (isDarkTheme) Color(0xFF00D9FF) else MaterialTheme.colorScheme.primary

    Scaffold(
        modifier = Modifier.background(bgColor),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AVibe",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceColor,
                    titleContentColor = textColor
                ),
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Переключить тему"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.padding(bottom = if (isPlayerVisible) 72.dp else 0.dp),
                containerColor = fabColor,
                contentColor = bgColor,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    hoveredElevation = 12.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(padding)
                .padding(bottom = if (isPlayerVisible) 64.dp else 0.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyLibraryMessage(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                        isDarkTheme = isDarkTheme
                    )
                }
            } else {
                items(items) { item ->
                    MediaItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onDelete = { onItemDelete(item) },
                        onRename = { newName -> onItemRename(item, newName) },
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}


// Empty library message when the library is empty
@Composable
fun EmptyLibraryMessage(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    isDarkTheme: Boolean
) {
    // Сhoosing a color depending on the theme
    val bgColor = if (isDarkTheme) Color(0xFF0A0E27) else MaterialTheme.colorScheme.background
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground

    androidx.compose.foundation.layout.Box(
        modifier = modifier.background(bgColor),
        contentAlignment = contentAlignment
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp),
                tint = NeonPurple.copy(alpha = 0.5f)
            )
            Text(
                "Библиотека пуста",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Нажмите + чтобы добавить файл",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
