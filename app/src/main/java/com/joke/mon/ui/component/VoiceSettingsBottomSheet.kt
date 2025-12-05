package com.joke.mon.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joke.mon.core.util.Config.VOICE_ID_CUTE
import com.joke.mon.core.util.Config.VOICE_ID_FEMALE
import com.joke.mon.core.util.Config.VOICE_ID_MALE
import com.joke.mon.core.util.Config.VOICE_ID_MYSTERY
import com.joke.mon.core.util.Config.VOICE_ID_QUICK
import com.joke.mon.core.util.Config.VOICE_ID_SLOW
import com.joke.mon.ui.theme.BlackAlpha32
import com.joke.mon.ui.theme.JokeMonTheme
import com.joke.mon.ui.theme.Primary500
import com.joke.mon.ui.theme.Secondary500
import com.joke.mon.ui.theme.Secondary900
import com.joke.mon.R
import com.joke.mon.core.data.model.PlaybackTarget
import com.joke.mon.core.data.model.VoiceConfig
import com.joke.mon.core.data.model.VoiceOption
import com.joke.mon.ui.theme.BlackAlpha08

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsBottomSheet(
    initialConfig: VoiceConfig,
    isLoading: Boolean,
    isGlobalPlaying: Boolean,
    onDismissRequest: () -> Unit,
    onApply: (VoiceConfig) -> Unit,
    onPreview: (VoiceConfig) -> Unit,
    onStop: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        VoiceSettingsContent(
            initialConfig = initialConfig,
            isLoading = isLoading,
            isGlobalPlaying = isGlobalPlaying,
            onDismiss = onDismissRequest,
            onApply = onApply,
            onPreview = onPreview,
            onStop = onStop
        )
    }
}

@Composable
fun VoiceSettingsContent(
    initialConfig: VoiceConfig,
    isLoading: Boolean,
    isGlobalPlaying: Boolean,
    onDismiss: () -> Unit,
    onApply: (VoiceConfig) -> Unit,
    onPreview: (VoiceConfig) -> Unit,
    onStop: () -> Unit
) {
    var selectedVoiceId by remember { mutableIntStateOf(initialConfig.voiceId) }
    var selectedSpeed by remember { mutableFloatStateOf(initialConfig.speed) }
    var selectedTone by remember { mutableStateOf(initialConfig.tone) }

    var playingVoiceId by remember { mutableStateOf<Int?>(null) }
    var currentPlaybackTarget by remember { mutableStateOf(PlaybackTarget.NONE) }

    val basicVoices = listOf(
        VoiceOption(VOICE_ID_MALE, stringResource(R.string.voice_male), "Basic"),
        VoiceOption(VOICE_ID_FEMALE, stringResource(R.string.voice_female), "Basic")
    )
    val transformVoices = listOf(
        VoiceOption(VOICE_ID_QUICK, stringResource(R.string.voice_quick), "Transform"),
        VoiceOption(VOICE_ID_SLOW, stringResource(R.string.voice_slow), "Transform"),
        VoiceOption(VOICE_ID_CUTE, stringResource(R.string.voice_cute), "Transform"),
        VoiceOption(VOICE_ID_MYSTERY, stringResource(R.string.voice_mystery), "Transform")
    )

    LaunchedEffect(isGlobalPlaying) {
        if (!isGlobalPlaying) {
            currentPlaybackTarget = PlaybackTarget.NONE
            playingVoiceId = null
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(BlackAlpha08, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_grey),
                        contentDescription = "닫기",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified,
                    )
                }

                Text(
                    text = stringResource(R.string.voice_style_settings),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column {
                SectionHeader(icon = "🎙️", title = stringResource(R.string.basic_voice))
                Spacer(modifier = Modifier.height(8.dp))

                basicVoices.forEach { voice ->
                    VoiceItemRow(
                        voice = voice,
                        isSelected = voice.id == selectedVoiceId,
                        isPlaying = currentPlaybackTarget == PlaybackTarget.VOICE && playingVoiceId == voice.id,
                        onPlayToggle = {
                            if (currentPlaybackTarget == PlaybackTarget.VOICE && playingVoiceId == voice.id) {
                                currentPlaybackTarget = PlaybackTarget.NONE
                                playingVoiceId = null
                                onStop()
                            } else {
                                currentPlaybackTarget = PlaybackTarget.VOICE
                                playingVoiceId = voice.id

                                onPreview(
                                    VoiceConfig(
                                        voiceId = voice.id,
                                        speed = selectedSpeed,
                                        tone = selectedTone
                                    )
                                )
                            }
                        },
                        onSelect = { selectedVoiceId = voice.id }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(icon = "🎭", title = stringResource(R.string.transformation_voice))

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.transformation_voice_description),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp , color =BlackAlpha32 ),
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                transformVoices.forEach { voice ->
                    VoiceItemRow(
                        voice = voice,
                        isSelected = voice.id == selectedVoiceId,
                        isPlaying = currentPlaybackTarget == PlaybackTarget.VOICE && playingVoiceId == voice.id,
                        onPlayToggle = {
                            if (currentPlaybackTarget == PlaybackTarget.VOICE && playingVoiceId == voice.id) {
                                currentPlaybackTarget = PlaybackTarget.NONE
                                playingVoiceId = null

                                onStop()
                            } else {
                                currentPlaybackTarget = PlaybackTarget.VOICE
                                playingVoiceId = voice.id

                                onPreview(
                                    VoiceConfig(
                                        voiceId = voice.id,
                                        speed = selectedSpeed,
                                        tone = selectedTone
                                    )
                                )
                            }
                        },
                        onSelect = { selectedVoiceId = voice.id }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(icon = "⚙️", title = stringResource(R.string.detailed_adjustment))
                Spacer(modifier = Modifier.height(12.dp))


                DetailAdjustmentRow(
                    label = stringResource(R.string.speed_text),
                    selectedValue = selectedSpeed,
                    options = listOf(0.5f, 1.0f, 2.0f),
                    displayTransform = { "${it}x" },
                    onValueChange = { selectedSpeed = it },
                    isPlaying = currentPlaybackTarget == PlaybackTarget.SPEED,
                    onPlayToggle = {
                        if (currentPlaybackTarget == PlaybackTarget.SPEED) {
                            currentPlaybackTarget = PlaybackTarget.NONE
                            onStop()
                        } else {
                            currentPlaybackTarget = PlaybackTarget.SPEED
                            onPreview(
                                VoiceConfig(
                                    voiceId = selectedVoiceId,
                                    speed = selectedSpeed,
                                    tone = selectedTone
                                )
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                DetailAdjustmentRow(
                    label = stringResource(R.string.select_tone),
                    selectedValue = selectedTone,
                    options = listOf("밝음", "보통", "낮음"),
                    displayTransform = { it },
                    onValueChange = { selectedTone = it },
                    isPlaying = currentPlaybackTarget == PlaybackTarget.TONE,
                    onPlayToggle = {
                        if (currentPlaybackTarget == PlaybackTarget.TONE) {
                            currentPlaybackTarget = PlaybackTarget.NONE
                            onStop() // [호출]
                        } else {
                            currentPlaybackTarget = PlaybackTarget.TONE
                            onPreview(
                                VoiceConfig(
                                    voiceId = selectedVoiceId,
                                    speed = selectedSpeed,
                                    tone = selectedTone
                                )
                            )
                        }
                    }

                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            CommonButton(
                stringResource(R.string.apply_text),
                isEnabled = true,
                onClick = {
                    val resultConfig = VoiceConfig(
                        voiceId = selectedVoiceId,
                        speed = selectedSpeed,
                        tone = selectedTone
                    )
                    onApply(resultConfig)
                    onDismiss()
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.5f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Primary500
                )
            }
        }
    }
}

@Composable
fun SectionHeader(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
        )
    }
}

@Composable
fun VoiceItemRow(
    voice: VoiceOption,
    isSelected: Boolean,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = voice.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.widthIn(min = 100.dp) // 텍스트 영역 확보
            )
            Spacer(modifier = Modifier.width(8.dp))


            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Secondary500)
                    .clickable { onPlayToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "정지" else "재생",
                    tint = Secondary900,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        SelectionIcon(isSelected = isSelected)
    }
}

@Composable
fun <T> DetailAdjustmentRow(
    label: String,
    selectedValue: T,
    options: List<T>,
    displayTransform: (T) -> String,
    onValueChange: (T) -> Unit,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Secondary500)
                    .clickable { onPlayToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = "재생",
                    tint = Secondary900,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            options.forEachIndexed { index, option ->
                val isOptionSelected = option == selectedValue

                Text(
                    text = displayTransform(option),
                    fontSize = 14.sp,
                    fontWeight = if (isOptionSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isOptionSelected) Primary500 else BlackAlpha32,
                    modifier = Modifier
                        .clickable { onValueChange(option) }
                        .padding(horizontal = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SelectionIcon(isSelected: Boolean) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Primary500),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "선택됨",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)), // 연한 회색 배경
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "선택안됨",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceSettingsPreview() {
    JokeMonTheme {
        Surface {
            VoiceSettingsContent(
                VoiceConfig(0, 1.0f, "보통") ,
                false,
                false,
                onDismiss = {} ,
                {},
                {},
                {}
                )
        }
    }
}