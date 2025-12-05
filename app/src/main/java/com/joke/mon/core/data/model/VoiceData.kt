package com.joke.mon.core.data.model

import com.joke.mon.core.util.Config.VOICE_ID_CUTE
import com.joke.mon.core.util.Config.VOICE_ID_FEMALE
import com.joke.mon.core.util.Config.VOICE_ID_MALE
import com.joke.mon.core.util.Config.VOICE_ID_MYSTERY
import com.joke.mon.core.util.Config.VOICE_ID_QUICK
import com.joke.mon.core.util.Config.VOICE_ID_SLOW

data class VoiceConfig(
    val voiceId: Int = 0,
    val speed: Float = 1.0f,
    val tone: String = "보통"
)

object VoiceConstants {
    val voiceOptions = listOf(
        VoiceOption(VOICE_ID_MALE, "남성 기본 목소리", "Basic"),
        VoiceOption(VOICE_ID_FEMALE, "여성 기본 목소리", "Basic"),
        VoiceOption(VOICE_ID_QUICK, "속사포 쇼츠 목소리", "Transform"),
        VoiceOption(VOICE_ID_SLOW, "느릿느릿 명상가 목소리", "Transform"),
        VoiceOption(VOICE_ID_CUTE, "귀여운 어린이 목소리", "Transform"),
        VoiceOption(VOICE_ID_MYSTERY, "미스테리한 외계인 목소리", "Transform")
    )

    fun getVoiceNameById(id: Int): String {
        return voiceOptions.find { it.id == id }?.title ?: "알 수 없음"
    }
}

data class VoiceOption(
    val id: Int,
    val title: String,
    val category: String
)

enum class PlaybackTarget {
    NONE,   // 정지 상태
    VOICE,  // 목록의 목소리 재생 중
    SPEED,  // 속도 예시 재생 중
    TONE    // 톤 예시 재생 중
}