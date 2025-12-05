package com.joke.mon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.joke.mon.R


// Set of Material typography styles to start with

val lineSeedKr = FontFamily(
    Font(R.font.line_seed_kr_bd , FontWeight.W700),
    Font(R.font.line_seed_kr_rg , FontWeight.W400),
    Font(R.font.line_seed_kr_th , FontWeight.W100)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = lineSeedKr,
        fontWeight = FontWeight.W700,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        color = GrayscaleBlack
    ),
    bodyMedium = TextStyle(
        fontFamily = lineSeedKr,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        color = GrayscaleBlack
    ),
    bodySmall = TextStyle(
        fontFamily = lineSeedKr,
        fontWeight = FontWeight.W100,
        fontSize = 12.sp,
        letterSpacing = 0.sp,
        color = GrayscaleBlack
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)