package com.joke.mon.core.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

object Dp {

    // 기본 간격 (8의 배수 사용)
    val spacing_2 = 2.dp
    val spacing_4 = 4.dp
    val spacing_8 = 8.dp
    val spacing_12 = 12.dp
    val spacing_16 = 16.dp
    val spacing_24 = 24.dp
    val spacing_32 = 32.dp

    val app_bar_height = 60.dp

    val common_button_height = 64.dp

    val screenPadding = PaddingValues(horizontal = spacing_16, vertical = spacing_16)
    val componentPadding = PaddingValues(all = spacing_16)
    val horizontalPadding = PaddingValues(horizontal = spacing_16)
    val verticalPadding = PaddingValues(vertical = spacing_8)


}