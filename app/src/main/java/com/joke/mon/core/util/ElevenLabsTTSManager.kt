package com.joke.mon.core.util

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import com.joke.mon.core.data.model.VoiceConfig
import com.joke.mon.core.data.soruce.remote.api.ElevenLabsService
import com.joke.mon.core.data.soruce.remote.dto.ElevenLabsRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElevenLabsTTSManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ElevenLabsService
)
{
    private val voiceMap = mapOf(
        Config.VOICE_ID_MALE to "1W00IGEmNmwmsDeYy7ag",
        Config.VOICE_ID_FEMALE to "uyVNoMrnUku1dZyVEXwD",
        Config.VOICE_ID_QUICK to "yjJ45q8TVCrtMhEKurxY",
        Config.VOICE_ID_SLOW to "HZTk7bUIkiI7yT7FKH4h",
        Config.VOICE_ID_CUTE to "B8gJV1IhpuegLxdpXFOE",
        Config.VOICE_ID_MYSTERY to "YOq2y2Up4RgXP2HyXjE5"
    )
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    suspend fun speak(text: String, voiceId: Int, config: VoiceConfig) {
        if (_isPlaying.value) {
            stop()
        }

        _isPlaying.value = true

        try {
            val voiceLinkId = voiceMap[voiceId] ?: voiceMap.values.first()

            var style = 1.0
            var stability = 1.0

            when (config.tone) {
                "보통" -> {
                    style = 0.5
                    stability = 0.6
                }
                "밝음" -> {
                    style = 1.0
                    stability = 0.3
                }
                "낮음" -> {
                    style = 0.1
                    stability = 0.9
                }
            }


            val request = ElevenLabsRequest(
                text = text,
                voiceId = voiceLinkId,
                style = style,
                stability = stability
            )

            val responseBody = apiService.generateSpeech(request)

            playAudioFromResponse(responseBody, config.speed)

        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    private suspend fun playAudioFromResponse(body: ResponseBody, speed: Float) = withContext(
        Dispatchers.IO) {
        try {

            val tempFile = File(context.cacheDir, "output.mp3")

            body.byteStream().use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            withContext(Dispatchers.Main) {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.path)
                    prepare()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val params = PlaybackParams()
                        params.speed = speed
                        playbackParams = params
                    }

                    setOnCompletionListener {
                        _isPlaying.value = false
                    }

                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.reset()
            _isPlaying.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
    }
}