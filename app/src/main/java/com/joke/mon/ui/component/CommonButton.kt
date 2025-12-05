package com.joke.mon.ui.component

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joke.mon.R
import com.joke.mon.core.util.Dp
import com.joke.mon.ui.theme.Grayscale300
import com.joke.mon.ui.theme.GrayscaleWhite
import com.joke.mon.ui.theme.JokeMonTheme
import com.joke.mon.ui.theme.WhiteAlpha32

@Composable
fun CommonButton(
    buttonText: String,
    image: Painter? = null,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit)
{
    val buttonColor = if (isEnabled) MaterialTheme.colorScheme.primary else Grayscale300
    val glowColor = buttonColor.copy(alpha = 0.5f)

    val blurRadius = 24.dp
    val blurRadiusPx = with(LocalDensity.current) { blurRadius.toPx() }

    val offsetY = with(LocalDensity.current) { 4.dp.toPx() }
    val expandX = with(LocalDensity.current) { 0.dp.toPx() }
    val expandY = with(LocalDensity.current) { 0.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dp.common_button_height)
            .drawBehind {
                if (isEnabled) {
                    val paint = Paint().apply {
                        color = glowColor
                        asFrameworkPaint().setMaskFilter(
                            BlurMaskFilter(
                                blurRadiusPx,
                                BlurMaskFilter.Blur.NORMAL
                            )
                        )
                    }

                    drawIntoCanvas { canvas ->
                        canvas.drawRoundRect(
                            left = -expandX / 2,
                            right = size.width + expandX / 2,
                            top = offsetY - expandY / 2,
                            bottom = size.height + offsetY + expandY / 2,
                            radiusX = 50.dp.toPx(),
                            radiusY = 50.dp.toPx(),
                            paint = paint
                        )
                    }
                }
            }
    ) {
        Button(
            enabled = isEnabled,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dp.common_button_height),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = WhiteAlpha32
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (image != null) {
                    Icon(
                        painter = image,
                        contentDescription = buttonText,
                        modifier = Modifier.size(Dp.spacing_24),
                        tint = GrayscaleWhite
                    )
                    Spacer(modifier = Modifier.width(Dp.spacing_8))
                }

                Text(
                    textAlign = TextAlign.Center,
                    text = buttonText,
                    style = MaterialTheme.typography.bodyMedium.copy(color = GrayscaleWhite, fontWeight = FontWeight.W700)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewCustomStyledButton() {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        JokeMonTheme {
            CommonButton("친구와 함께 웃기" , isEnabled = true ,onClick = {})
            Spacer(modifier = Modifier.size(32.dp))
            CommonButton("친구와 함께 웃기" , painterResource(R.drawable.ic_bottom_home_default), isEnabled = false , onClick = {})
        }

    }
}