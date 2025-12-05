package com.joke.mon.core.data.model

import androidx.annotation.DrawableRes

data class JokeCategory(
    val id : Int,
    val text: String,
    val emoji: String? = null
)
