package com.joke.mon.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joke.mon.core.util.Dp
import com.joke.mon.ui.theme.BlackAlpha08
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.JokeMonTheme

@Composable
fun CommonAppBar(
    modifier: Modifier = Modifier,
    title: String,
    leftIconPainter: Painter? = null,
    onLeftIconClick: () -> Unit = {},
    rightIconPainter: Painter? = null,
    onRightIconClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dp.app_bar_height)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (leftIconPainter != null) {
                AppBarIcon(
                    painter = leftIconPainter,
                    onClick = onLeftIconClick,
                    contentDescription = "Left Icon"
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rightIconPainter != null) {
                AppBarIcon(
                    painter = rightIconPainter,
                    onClick = onRightIconClick,
                    contentDescription = "Right Icon"
                )
            }
        }
    }
}

@Composable
private fun AppBarIcon(
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String
) {
    IconButton(onClick = onClick) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(BlackAlpha08, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painter,
                contentDescription = contentDescription,
                tint = GrayscaleBlack,
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CommonAppBarPreview() {
    JokeMonTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
            CommonAppBar(
                title = "농담 생성 결과",
                leftIconPainter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                onLeftIconClick = { /* 뒤로가기 동작 */ },
                rightIconPainter = rememberVectorPainter(Icons.Default.Home),
                onRightIconClick = { /* 홈으로 이동 동작 */ }
            )

            Spacer(modifier = Modifier.height(10.dp))

            CommonAppBar(
                title = "뒤로가기",
                leftIconPainter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CommonAppBar(
                title = "홈",
                rightIconPainter = rememberVectorPainter(Icons.Default.Home)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CommonAppBar(
                title = "제목만"
            )
        }
    }

}