package com.example.avibe.ui.theme.player

import android.media.audiofx.EnvironmentalReverb
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

// Audio effects engine for media player
@OptIn(UnstableApi::class)
class AudioEffectsEngine(private val player: ExoPlayer) {
    private var environmentalReverb: EnvironmentalReverb? = null
    private var isInitialized = false
    private var reverbLevel: Float = 0f

    private val TAG = "AudioEffectsEngine"

    fun init() {
        if (isInitialized) return

        try {
            val sessionId = player.audioSessionId
            if (sessionId == 0) {
                Log.w(TAG, "AudioSessionId is 0, cannot init reverb yet")
                return
            }

            environmentalReverb?.release()

            environmentalReverb = EnvironmentalReverb(0, sessionId).apply {
                enabled = false
            }

            isInitialized = true
            Log.d(TAG, "Reverb initialized successfully")
            applyReverbSettings()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to init reverb: ${e.message}")
            isInitialized = false
        }
    }

    /*
    fun release() {
        try {
            environmentalReverb?.release()
        } catch (e: Exception) { /* Ignore */ }
        environmentalReverb = null
        isInitialized = false
    }
    */

    /**
     * Set the playback speed and pitch.
     * @param speed The playback speed.
     */
    fun setSlowed(speed: Float) {
        val pitch = if (speed < 1.0f) speed * 0.95f else speed
        player.playbackParameters = PlaybackParameters(speed, pitch)
    }

    /**
     * Set the reverb level.
     * @param level The reverb level.
     */
    fun setReverb(level: Float) {
        this.reverbLevel = level.coerceIn(0f, 100f)
        applyReverbSettings()
    }

    private fun applyReverbSettings() {
        if (!isInitialized) init()
        if (!isInitialized) return

        val intensity = reverbLevel / 100f // 0.0 - 1.0

        environmentalReverb?.let { rev ->
            try {
                if (intensity > 0) {
                    rev.enabled = true

                    // --- "CONCERT HALL" LOGIC ---

                    // 1. DecayTime
                    // Normal: 2-4 sec.
                    // CONCERT (80%+): A sharp jump to 6-8 seconds for a long tail.
                    val decayTime = if (reverbLevel >= 80) {
                        // Экспоненциальный рост для режима концерта
                        (6000 + ((reverbLevel - 80) / 20) * 2000).toInt()
                    } else {
                        // Плавный рост для обычных значений
                        (2000 + (intensity * 3000)).toInt()
                    }

                    // 2. RoomLevel
                    // CRITICAL: Leave it at 0.
                    // This parameter creates the "subwoofer on" effect and the hum.
                    val roomLevel = 0.toShort()

                    // 3. ReverbLevel (Effect Level)
                    // For a concert, we crank it up to the max (1000), but be careful.
                    val reverbLevelVal = if (reverbLevel >= 80) {
                        // На концерте эффект должен быть очень явным
                        (800 + ((reverbLevel - 80) / 20) * 200).toInt().toShort()
                    } else {
                        (intensity * 700).toInt().toShort()
                    }

                    // 4. Density & Diffusion
                    // For a hall, the maximum value (1000) is needed to ensure the sound is thick and not just echoed.
                    val density = 1000.toShort()
                    val diffusion = 1000.toShort()

                    // 5. DecayHFRatio (High Frequency Decay)
                    // In some rooms, high frequencies decay faster than low frequencies.
                    // It collects the "metallic ringing" and produces a soft sound.
                    val decayHFRatio = (400 + (intensity * 400)).toInt().toShort()

                    // Apply settings
                    rev.setDecayTime(decayTime)
                    rev.setRoomLevel(roomLevel)
                    rev.setReverbLevel(reverbLevelVal)
                    rev.setDensity(density)
                    rev.setDiffusion(diffusion)
                    rev.setDecayHFRatio(decayHFRatio)

                    Log.d(TAG, "Reverb Mode: ${if(reverbLevel >= 80) "CONCERT HALL" else "NORMAL"} | Decay=$decayTime ms | Level=$reverbLevelVal")

                } else {
                    rev.enabled = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error applying reverb settings: ${e.message}")
            }
        }
    }

    // Reset all settings (slowed and reverb)
    fun reset() {
        player.playbackParameters = PlaybackParameters(1f, 1f)
        setReverb(0f)
    }
}