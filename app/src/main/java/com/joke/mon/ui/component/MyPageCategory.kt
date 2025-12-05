package com.joke.mon.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joke.mon.R
import com.joke.mon.ui.theme.Grayscale400
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.JokeMonTheme

@Composable
fun MyPageCategory(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    text: String,
    iconContentDescription: String? = "",
    onClick: () -> Unit,
) {


    Row(modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(vertical = 2.dp)
        ,
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                tint = GrayscaleBlack,
                contentDescription = iconContentDescription,
            )



            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            tint = Grayscale400,
            contentDescription = "Move",
        )
    }

}

@Preview
@Composable
fun MyPageCategoryPreview() {
    JokeMonTheme {

        Column(Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
        ) {

            MyPageCategory(
                modifier = Modifier,
                R.drawable.ic_bottom_home_default,
                "테스트"
            ) {

            }
        }

    }
}