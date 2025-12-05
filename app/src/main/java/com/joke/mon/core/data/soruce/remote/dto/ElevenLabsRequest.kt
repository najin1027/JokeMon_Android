package com.joke.mon.core.data.soruce.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ElevenLabsRequest(
    @SerializedName("text")
    val text: String,
    @SerializedName("voiceId")
    val voiceId: String,
    @SerializedName("style")
    val style: Double,
    @SerializedName("stability")
    val stability: Double
)
