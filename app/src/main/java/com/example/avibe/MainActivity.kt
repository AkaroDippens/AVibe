package com.example.avibe

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.example.avibe.data.model.MediaItem
import com.example.avibe.data.model.MediaRepository
import com.example.avibe.data.model.MediaType
import com.example.avibe.ui.theme.AVibeTheme
import com.example.avibe.ui.theme.components.AddMediaDialog
import com.example.avibe.ui.theme.components.MiniPlayer
import com.example.avibe.ui.theme.library.LibraryScreen
import com.example.avibe.ui.theme.player.BottomPlayerSheet
import kotlinx.coroutines.launch

@UnstableApi
class MainActivity : ComponentActivity() {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private var playerNotificationManager: PlayerNotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaRepository = MediaRepository(this)
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            volume = 1.0f
        }

        createNotificationChannel()
        requestNotificationPermission()

        // Create a MediaSession for the player
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        initPlayerNotification()

        // Get the PlayerNotificationManager
        playerNotificationManager = getSystemService(PlayerNotificationManager::class.java)

        setContent {
            var mediaList by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
            var currentMedia by remember { mutableStateOf<MediaItem?>(null) }
            var isPlaying by remember { mutableStateOf(false) }
            var showAddDialog by remember { mutableStateOf(false) }
            var showPlayer by remember { mutableStateOf(false) }
            var isDarkTheme by remember { mutableStateOf(true) }
            var isThemeLoaded by remember { mutableStateOf(false) }

            var speed by remember { mutableStateOf(1f) }
            var reverb by remember { mutableStateOf(0f) }
            var pickType by remember { mutableStateOf<MediaType?>(null) }

            DisposableEffect(exoPlayer) {

                val listener = object : Player.Listener {
                    override fun onMediaItemTransition(
                        mediaItem: androidx.media3.common.MediaItem?,
                        reason: Int
                    ) {
                        val newMedia = mediaList.find { item ->
                            item.uri == mediaItem?.localConfiguration?.uri?.toString()
                        }
                        if (newMedia != null) {
                            currentMedia = newMedia
                            isPlaying = exoPlayer.isPlaying
                        }
                    }

                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                    }
                }
                exoPlayer.addListener(listener)

                onDispose {
                    exoPlayer.removeListener(listener)
                }
            }

            // Uploading media list
            LaunchedEffect(Unit) {
                mediaRepository.getMediaList().collect { list ->
                    mediaList = list
                }
            }

            // Uploading theme
            LaunchedEffect(Unit) {
                mediaRepository.getTheme().collect { isDark ->
                    isDarkTheme = isDark
                    isThemeLoaded = true
                }
            }

            val pickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri != null && pickType != null) {
                    try {
                        // Getting file name
                        val fileName = getFileName(uri) ?: "Медиа ${System.currentTimeMillis()}"

                        // Saving permissions to read URI
                        try {
                            contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (e: Exception) {
                            // Ignoring
                        }

                        val newItem = MediaItem(
                            id = System.currentTimeMillis().toString(),
                            name = fileName,
                            type = pickType!!,
                            uri = uri.toString(),
                            dateAdded = System.currentTimeMillis()
                        )

                        val updatedList = mediaList + newItem
                        mediaList = updatedList

                        // Saving in DataStore
                        lifecycleScope.launch {
                            try {
                                mediaRepository.saveMediaList(updatedList)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            AVibeTheme(isDarkTheme, dynamicColor = false) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isThemeLoaded) return@AVibeTheme

                    LibraryScreen(
                        items = mediaList,
                        isPlayerVisible = currentMedia != null,
                        onItemClick = { item ->
                            val mediaItems = mediaList.map { mediaItem ->
                                androidx.media3.common.MediaItem.Builder()
                                    .setUri(mediaItem.uri)
                                    .setMediaMetadata(
                                        androidx.media3.common.MediaMetadata.Builder()
                                            .setTitle(mediaItem.name)
                                            .setArtist(mediaItem.type.name)
                                            .build()
                                    )
                                    .build()
                            }
                            val startIndex = mediaList.indexOf(item)

                            exoPlayer.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
                            exoPlayer.prepare()

                            // Updating UI state
                            currentMedia = item
                            isPlaying = true
                            showPlayer = true

                            // Let the notification know that this is a turn on (turns on the Next/Previous buttons)
                            playerNotificationManager?.setUseNextAction(true)
                            playerNotificationManager?.setUsePreviousAction(true)
                        },
                        onAddClick = { showAddDialog = true },
                        onItemDelete = { item ->
                            lifecycleScope.launch {
                                mediaRepository.deleteMediaItem(item.id)
                            }
                        },
                        onItemRename = { item, newName ->
                            lifecycleScope.launch {
                                mediaRepository.renameMediaItem(item.id, newName)
                            }
                        },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            val newValue = !isDarkTheme
                            isDarkTheme = newValue
                            lifecycleScope.launch {
                                mediaRepository.saveTheme(newValue)
                            }
                        }
                    )

                    if (showAddDialog) {
                        AddMediaDialog(
                            onMp3 = {
                                pickType = MediaType.MP3
                                showAddDialog = false
                                pickerLauncher.launch(arrayOf("audio/mpeg", "audio/*"))
                            },
                            onMp4 = {
                                pickType = MediaType.MP4
                                showAddDialog = false
                                pickerLauncher.launch(arrayOf("video/mp4", "video/*"))
                            },
                            onDismiss = { showAddDialog = false },
                            isDarkTheme = isDarkTheme
                        )
                    }

                    // Mini player at the bottom of the screen
                    if (currentMedia != null && !showPlayer) {
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            MiniPlayer(
                                media = currentMedia,
                                isPlaying = isPlaying,
                                onPlayPause = {
                                    if (exoPlayer.isPlaying) exoPlayer.pause()
                                    else exoPlayer.play()
                                },
                                onClick = { showPlayer = true },
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }

                    // Full screen player
                    if (showPlayer) {
                        BottomPlayerSheet(
                            exoPlayer = exoPlayer,
                            media = currentMedia,
                            isPlaying = isPlaying,
                            speed = speed,
                            reverb = reverb,
                            onPlayPause = {
                                if (exoPlayer.isPlaying) exoPlayer.pause()
                                else exoPlayer.play()
                            },
                            onSpeedChange = { speed = it },
                            onReverbChange = { reverb = it },
                            onReset = {
                                speed = 1f
                                reverb = 0f
                            },
                            onDismiss = { showPlayer = false },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }

    // Create a notification channel (required for Android 8+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "avibe_playback",
                "Воспроизведение музыки",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Управление плеером AVibe"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    // Request notification permission (Android 13+)
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    // The user declined - the notification will not appear.
                    android.util.Log.w("AVibe", "Notification permission denied")
                }
            }
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Initializing a notification via PlayerNotificationManager
    private fun initPlayerNotification() {
        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            1, // notificationId
            "avibe_playback" // channelId (must match createNotificationChannel)
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {

                // 1. Notification Title (Track Title)
                override fun getCurrentContentTitle(player: Player): String {
                    return player.mediaMetadata.title?.toString() ?: "AVibe"
                }

                // 2. Clicking on the notification opens the app
                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return PendingIntent.getActivity(
                        this@MainActivity,
                        0,
                        Intent(this@MainActivity, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                // 3. Subtitle (file type / author)
                override fun getCurrentContentText(player: Player): String? {
                    return player.mediaMetadata.artist?.toString()
                        ?: player.mediaMetadata.mediaType?.toString()
                }

                // 4. Large icon
                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return null
                }
            })
            .setSmallIconResourceId(androidx.media3.session.R.drawable.media_session_service_notification_ic_music_note)
            .build()
            .also { manager ->
                manager.setPlayer(exoPlayer)
            }
    }

    // Releasing the player only when the Activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        // Releasing the notification
        playerNotificationManager?.setPlayer(null)

        // Releasing the session
        mediaSession.release()

        // Releasing the player
        exoPlayer.release()
    }

    // Getting the name of the file
    private fun getFileName(uri:Uri):String? {
        val documentFile = DocumentFile.fromSingleUri(this, uri)
        return documentFile?.name ?: uri.lastPathSegment
    }
}
