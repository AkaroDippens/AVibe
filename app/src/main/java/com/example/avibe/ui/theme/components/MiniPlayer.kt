package com.example.avibe.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avibe.data.model.MediaItem
import com.example.avibe.data.model.MediaType
import com.example.avibe.ui.theme.NeonCyan
import com.example.avibe.ui.theme.NeonPurple

// MiniPlayer for playing media
@Composable
fun MiniPlayer(
    media: MediaItem?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    if (media == null) return

    // Сhoosing a color depending on the theme
    val accentColor = if (media.type == MediaType.MP4) NeonPurple else NeonCyan
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1F3A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                color = surfaceColor,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored left stripe
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Information about media
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = media.name,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = media.type.name,
                color = textSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }

        // Play/Pause button
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
                tint = accentColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
