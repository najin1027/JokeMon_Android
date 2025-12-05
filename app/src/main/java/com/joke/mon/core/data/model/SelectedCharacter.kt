package com.joke.mon.core.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class SelectedCharacter(
    val name: String,
    val description: String,
    val color: Color,
    @DrawableRes val imageResId: Int
)
