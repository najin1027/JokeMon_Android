package com.joke.mon.feature.result_joke.presentation

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joke.mon.R
import com.joke.mon.core.data.model.JokeCategory
import com.joke.mon.core.data.model.VoiceConstants
import com.joke.mon.core.util.Utils.getImagePathByCategory
import com.joke.mon.core.util.Utils.getJokeBackgroundColor
import com.joke.mon.core.util.Utils.getJokeChipBackgroundColor
import com.joke.mon.ui.component.CommonAppBar
import com.joke.mon.ui.component.CommonButton
import com.joke.mon.ui.component.CommonLoading
import com.joke.mon.ui.component.CommonSnackBar
import com.joke.mon.ui.component.VoiceSettingsBottomSheet
import com.joke.mon.ui.navigation.AppRoute
import com.joke.mon.ui.theme.BlackAlpha08
import com.joke.mon.ui.theme.BlackAlpha64
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.GrayscaleWhite
import com.joke.mon.ui.theme.JokeMonTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun ResultJokeScreen (navController: NavController, viewModel : ResultJokeViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE)
            as android.content.ClipboardManager
    val snackBarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    var snackBarJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                ResultJokeContract.ResultJokeEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                ResultJokeContract.ResultJokeEffect.NavigateToHome ->  {
                    navController.navigate(AppRoute.MAIN) {
                        popUpTo(AppRoute.MAIN) {
                            inclusive = true
                        }
                    }
                }
                is ResultJokeContract.ResultJokeEffect.ShowSnackBar -> {
                    snackBarJob?.cancel()

                    snackBarJob = scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(effect.message)
                    }
                }

                is ResultJokeContract.ResultJokeEffect.CopyToClipboard -> {
                    val clip = ClipData.newPlainText("joke", effect.text)
                    clipboardManager.setPrimaryClip(clip)

                    snackBarJob?.cancel()
                    snackBarJob = scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(context.getString(R.string.copied_joke))
                    }
                }

                is ResultJokeContract.ResultJokeEffect.ShareJoke ->  {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_to_friends))
                    context.startActivity(shareIntent)
                }
            }
        }
    }
    
    ResultJokeContent(uiState , viewModel::sendEvent , snackBarHostState = snackBarHostState)

}


@Composable
fun ResultJokeContent(
    uiState: ResultJokeContract.ResultJokeState,
    onEvent: (ResultJokeContract.ResultJokeEvent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    Box(modifier = Modifier.fillMaxSize()) {

    val scrollState = rememberScrollState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            ) { snackBarData ->
                CommonSnackBar(snackBarData = snackBarData)
            }
        },
        topBar = {
            CommonAppBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .statusBarsPadding(),
                title = stringResource(R.string.result_create_joke),
                leftIconPainter = painterResource(R.drawable.ic_back_arrow),
                rightIconPainter = painterResource(R.drawable.ic_home_black),
                onLeftIconClick = { onEvent(ResultJokeContract.ResultJokeEvent.OnBackClicked) },
                onRightIconClick = { onEvent(ResultJokeContract.ResultJokeEvent.OnHomeClicked) }
            )
        }
    ) { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val contentToShow: String =
                    if (uiState.isLoading) stringResource(R.string.generating_joke_placeholder)
                    else uiState.jokeContent
                val isLikedToShow: Boolean = if (uiState.isLoading) false else uiState.isLiked
                val categoryToShow: JokeCategory? = uiState.category
                val categoryIdToShow: Int = uiState.jokeCategoryId


                    JokeSpeechBubbleCard(
                        jokeContent = contentToShow,
                        jokeCategoryId = categoryIdToShow,
                        isLiked = isLikedToShow,
                        category = categoryToShow,

                        onLikeToggle = {
                            if (!uiState.isLoading) {
                                onEvent(ResultJokeContract.ResultJokeEvent.OnLikeToggled)
                            }
                        },
                        onCopyClick = { content ->
                            if (!uiState.isLoading) {
                                onEvent(ResultJokeContract.ResultJokeEvent.OnCopyClicked(content))
                            }
                        },
                        onRegenerateClick = {
                            if (!uiState.isLoading) {
                                onEvent(ResultJokeContract.ResultJokeEvent.OnRegenerateClicked)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Image(
                        painter = getImagePathByCategory(uiState.jokeCategoryId , uiState.selectedCharacterIndex),
                        contentDescription = ""
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .background(BlackAlpha08, shape = RoundedCornerShape(16.dp)),
                        Arrangement.Start,
                        Alignment.CenterVertically
                    ) {

                        Spacer(modifier = Modifier.width(16.dp))

                        PlayPauseButton(uiState.isJokePlaying , onClick = {
                               onEvent(ResultJokeContract.ResultJokeEvent.OnPlayJokeClicked)
                        })

                        Spacer(modifier = Modifier.width(12.dp))

                        val voiceName = VoiceConstants.getVoiceNameById(uiState.voiceConfig.voiceId)

                        Text("음성 스타일: $voiceName" ,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))


                        IconButton(onClick = {
                            onEvent(ResultJokeContract.ResultJokeEvent.OnOpenVoiceSettings)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = "settings",
                                tint = Color.Unspecified
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    CommonButton(
                        buttonText = stringResource(R.string.share_to_friends),
                        image = painterResource(R.drawable.ic_share),
                        isEnabled = true
                    ) {
                            onEvent(ResultJokeContract.ResultJokeEvent.OnShareClicked)
                    }
            }
    }
        CommonLoading(uiState.isLoading || uiState.isVoiceLoading)

        if (uiState.isVoiceSheetVisible) {
            VoiceSettingsBottomSheet(
                initialConfig = uiState.voiceConfig,
                isLoading = uiState.isVoiceLoading,
                isGlobalPlaying = uiState.isJokePlaying,
                onDismissRequest = {
                    onEvent(ResultJokeContract.ResultJokeEvent.OnCloseVoiceSettings)
                },
                onApply = { newConfig ->
                    onEvent(ResultJokeContract.ResultJokeEvent.OnApplyVoiceSettings(newConfig))
                },

                onPreview = { previewConfig ->
                    onEvent(ResultJokeContract.ResultJokeEvent.OnPreviewVoice(previewConfig))
                },

                onStop = {
                    onEvent(ResultJokeContract.ResultJokeEvent.OnStopVoice)
                }
            )
        }
    }
}

@Composable
fun JokeSpeechBubbleCard(
    jokeContent: String,
    jokeCategoryId: Int,
    isLiked: Boolean,
    category: JokeCategory?,
    onLikeToggle: () -> Unit,
    onCopyClick: (String) -> Unit,
    onRegenerateClick: () -> Unit
) {
    val cardBackgroundColor = getJokeBackgroundColor(jokeCategoryId)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    if (category != null) {
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            JokeItemCategoryChip(category = category)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(36.dp))
                    }

                    LikeButton(
                        isLiked = isLiked,
                        onClick = onLikeToggle,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    textAlign = TextAlign.Center,
                    text = jokeContent,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    ),
                    color = GrayscaleBlack,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 하단 버튼 (중앙 정렬)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    ActionButton(
                        text = stringResource(R.string.copy_text),
                        iconRes = R.drawable.ic_copy,
                        onClick = { onCopyClick(jokeContent) }
                    )

                    Spacer(Modifier.width(8.dp))

                    ActionButton(
                        text = stringResource(R.string.regeneration),
                        iconRes = R.drawable.ic_retry,
                        onClick = onRegenerateClick
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = (-15).dp, y = (-10).dp)
                .size(width = 40.dp, height = 30.dp)
                .drawBehind {
                    val path = Path().apply {

                        val xEnd = size.width
                        val yEnd = 0f
                        val yTip = size.height

                        val xTip = size.width * 0.9f // 16f


                        val x1_left = xTip * 0.8f // 12.8f


                        val x1_right = xTip + (xEnd - xTip) * 0.2f


                        moveTo(0f, 0f)


                        quadraticBezierTo(
                            x1 = x1_left,
                            y1 = yTip * 0.8f,
                            x2 = xTip,
                            y2 = yTip
                        )

                        quadraticBezierTo(
                            x1 = x1_right,
                            y1 = yTip * 0.8f,
                            x2 = xEnd,
                            y2 = yEnd
                        )

                        close()
                    }
                    drawPath(
                        path = path,
                        color = cardBackgroundColor
                    )
                }
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    val buttonColor = BlackAlpha64

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(buttonColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp , color = GrayscaleWhite)
        )
    }
}


@Composable
fun LikeButton(
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
    ) {
        Icon(
            painter = if(isLiked)painterResource(R.drawable.ic_circle_like_fill_large)
            else painterResource(R.drawable.ic_circle_like_line_large),
            tint = Color.Unspecified,
            contentDescription = "Like",
        )
    }
}

@Composable
fun JokeItemCategoryChip(
    category: JokeCategory
) {
    val chipBackgroundColor = getJokeChipBackgroundColor(category.id)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(chipBackgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = category.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp , fontWeight = W700)
            )
            if (category.emoji != null) {
                Text(
                    text = category.emoji,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp , fontWeight = W700)
                )
            }
        }
    }
}

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 40.dp,
    iconSize: Dp = 32.dp
) {
    val buttonColor = Color(0xFF5B1BD0)

    Box(
        modifier = modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(buttonColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) {
                Icons.Filled.Stop
            } else {
                Icons.Filled.PlayArrow
            },
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
private fun PlayPauseButtonPreview() {
    PlayPauseButton(true , {})
}


@Preview(name = "Content - Success", showBackground = true)
@Composable
fun ResultJokeContentSuccessPreview() {
    val mockCategory = JokeCategory(id = 2, text = "아재개그", emoji = "👨‍🦰")
    val mockContent = "회의시간에 졸다 지적받은 동료가 이야기했어요.\n“죄송합니다, 제가 회의 내용을 꿈속에서 더 잘 이해했네요!”"
    val successState = ResultJokeContract.ResultJokeState(
        isLoading = false,
        jokeContent = mockContent,
        jokeCategoryId = 2,
        isLiked = true,
        category = mockCategory
    )
    val snackBarHostState = SnackbarHostState()

    JokeMonTheme {
        Surface(Modifier.fillMaxSize()) {
            ResultJokeContent(
                uiState = successState,
                onEvent = {} ,
                snackBarHostState
            )
        }
    }
}

@Preview(name = "Content - Loading", showBackground = true)
@Composable
fun ResultJokeContentLoadingPreview() {
    // 로딩 상태에 대한 가짜(mock) State 생성
    val loadingState = ResultJokeContract.ResultJokeState(
        isLoading = true,
        jokeCategoryId = 3
    )

    val snackBarHostState = SnackbarHostState()

    JokeMonTheme {
        Surface(Modifier.fillMaxSize()) {
            ResultJokeContent(
                uiState = loadingState,
                onEvent = {},
                snackBarHostState
            )
        }
    }
}

@Preview
@Composable
fun ActionBtnPreview() {
    JokeMonTheme {
        ActionButton("재생성" , R.drawable.ic_home_black) {

        }
    }
}