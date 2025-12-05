package com.joke.mon.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joke.mon.R
import com.joke.mon.ui.theme.Grayscale300
import com.joke.mon.ui.theme.Grayscale500
import com.joke.mon.ui.theme.Grayscale900
import com.joke.mon.ui.theme.JokeMonTheme

@Composable
fun ReviewRequestDialog(
    onWriteReview: () -> Unit,
    onCancel: () -> Unit,
    onExit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(vertical = 28.dp, horizontal = 24.dp)
    ) {

        Column() {
            Text(
                text = stringResource(R.string.review_dialog_title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.review_dialog_msg),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = Grayscale900
                ),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onWriteReview() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.write_review),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(modifier = Modifier
                    .padding(vertical = 10.dp , horizontal = 12.dp)
                    .clickable { onCancel() }
                ) {
                    Text(
                        text = stringResource(R.string.cancel_btn),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = Grayscale500
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier
                    .padding(vertical = 10.dp , horizontal = 12.dp)
                    .clickable { onExit() }
                ) {
                    Text(
                        text = stringResource(R.string.exit_btn),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ExitDialog(
    onExit: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(vertical = 28.dp, horizontal = 24.dp)
    ) {

        Column() {
            Text(
                text = "농담몬 종료",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.exit_dialog_msg),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                ),
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 취소 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Grayscale300,
                            RoundedCornerShape(16.dp))
                        .background(Color.Transparent)
                        .clickable { onCancel() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.cancel_btn),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = Grayscale900
                        )
                    )
                }

                // 종료 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onExit() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "종료",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ExitDialogPreview() {
    JokeMonTheme {
        ExitDialog( {} , {})
    }
}


@Preview
@Composable
fun ReviewRequestDialogPreview () {
    JokeMonTheme {
        ReviewRequestDialog(
            {},
            {}
        ) { }
    }
}