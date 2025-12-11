package com.joke.mon.feature.home.data.source.remote.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeywordResponse(
    @SerialName("keywords")
    val keywords: List<String>
)