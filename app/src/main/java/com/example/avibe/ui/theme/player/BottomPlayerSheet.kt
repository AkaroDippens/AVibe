package com.example.avibe.ui.theme.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.avibe.data.model.MediaType
import com.example.avibe.ui.theme.NeonCyan
import com.example.avibe.ui.theme.NeonPink
import com.example.avibe.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import com.example.avibe.data.model.MediaItem as AppMediaItem

// Bottom player sheet for media player
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPlayerSheet(
    exoPlayer: ExoPlayer,
    media: AppMediaItem?,
    isPlaying: Boolean,
    speed: Float,
    reverb: Float,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onReverbChange: (Float) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    if (media == null) return

    // Сhoosing a color depending on the theme
    val bgColor = if (isDarkTheme) Color(0xFF0A0E27) else MaterialTheme.colorScheme.background
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1F3A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurface

    var isEngineReady by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }

    val effectsEngine = remember { AudioEffectsEngine(exoPlayer) }

    // Choosing a color depending on the media type
    val accentColor = if (media.type == MediaType.MP4) NeonPurple else NeonCyan

    // Initialize the effects engine
    DisposableEffect(media) {
        exoPlayer.playWhenReady = isPlaying

        val listener = object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                if (audioSessionId != 0) {
                    effectsEngine.init()
                    isEngineReady = true
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = exoPlayer.duration
                }
            }
        }
        exoPlayer.addListener(listener)

        if (exoPlayer.audioSessionId != 0) {
            effectsEngine.init()
            isEngineReady = true
        }

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // Update the current position and duration (if not seeking)
    LaunchedEffect(isPlaying, isSeeking) {
        while (isPlaying && !isSeeking) {
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.takeIf { it > 0 } ?: duration
            delay(500)
        }
    }

    LaunchedEffect(speed) {
        effectsEngine.setSlowed(speed)
    }

    LaunchedEffect(reverb) {
        if (isEngineReady) {
            effectsEngine.setReverb(reverb)
        }
    }

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.height(if (media.type == MediaType.MP4) 700.dp else 550.dp),
        containerColor = surfaceColor,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Video player для MP4
            if (media.type == MediaType.MP4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            color = bgColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Информация о медиа
            Text(
                text = media.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1
            )
            Text(
                text = media.type.name,
                fontSize = 13.sp,
                color = textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Seek bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatTime(currentPosition),
                    fontSize = 11.sp,
                    color = textSecondary
                )

                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { newValue ->
                        currentPosition = newValue.toLong()
                        isSeeking = true
                    },
                    onValueChangeFinished = {
                        isSeeking = false
                        exoPlayer.seekTo(currentPosition)
                    },
                    valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = accentColor.copy(alpha = 0.3f)
                    )
                )

                Text(
                    text = formatTime(duration),
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Spacer(Modifier.height(28.dp))

            // Speed control
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Скорость",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                    Text(
                        "${"%.2f".format(speed)}x",
                        fontSize = 14.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = speed,
                    onValueChange = { newValue ->
                        val stepSize = 0.05f
                        val stepped = (newValue / stepSize).toInt() * stepSize
                        onSpeedChange(stepped)
                    },
                    valueRange = 0.5f..2f,
                    steps = ((2f - 0.5f) / 0.05f).toInt() - 1,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = accentColor.copy(alpha = 0.3f),
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            // Reverb control
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Реверберация",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                    Text(
                        "${"%.0f".format(reverb)}%",
                        fontSize = 14.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = reverb,
                    onValueChange = onReverbChange,
                    valueRange = 0f..100f,
                    steps = 100,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = accentColor.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = {
                        effectsEngine.reset()
                        onReset()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = NeonPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Сбросить",
                        tint = NeonPink,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
