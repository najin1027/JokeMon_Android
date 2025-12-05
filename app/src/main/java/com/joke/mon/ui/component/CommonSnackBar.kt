package com.joke.mon.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joke.mon.ui.theme.BlackAlpha80
import com.joke.mon.ui.theme.JokeMonTheme

@Composable
fun CommonSnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier)
{
        Surface(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp , vertical = 8.dp),
            color = BlackAlpha80,
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = snackBarData.visuals.message,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = Color.White),
                modifier = Modifier.padding(vertical = 14.dp , horizontal = 16.dp),
                textAlign = TextAlign.Start
            )
        }
}