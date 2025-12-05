package com.joke.mon.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joke.mon.R
import com.joke.mon.core.util.Dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.joke.mon.core.util.Config
import com.joke.mon.core.util.Config.GILL_GILL_MON
import com.joke.mon.core.util.Config.HEE_HEE_MON
import com.joke.mon.core.util.Config.KICK_KICK_MON
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse
import com.joke.mon.feature.home.presentation.HomeContract.NicknameDialogState
import com.joke.mon.feature.result_joke.presentation.CATEGORY_ID_KEY
import com.joke.mon.feature.result_joke.presentation.ENTRY_POINT_KEY
import com.joke.mon.feature.result_joke.presentation.KEYWORD_KEY
import com.joke.mon.feature.result_joke.presentation.ResultJokeEntryPoint
import com.joke.mon.ui.component.CommonButton
import com.joke.mon.ui.component.CommonLoading
import com.joke.mon.ui.navigation.ResultJokeRoute
import com.joke.mon.ui.theme.BlackAlpha80
import com.joke.mon.ui.theme.Grayscale300
import com.joke.mon.ui.theme.Grayscale400
import com.joke.mon.ui.theme.Grayscale500
import com.joke.mon.ui.theme.Grayscale700
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.JokeMonTheme
import com.joke.mon.ui.theme.SemanticError
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

@Composable
fun HomeScreen(navController: NavController, viewModel : HomeViewModel = hiltViewModel() , showSnackBar: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeContract.HomeEffect.ShowSnackBar -> {
                    showSnackBar(effect.message.resolve(context = context))
                }

                is HomeContract.HomeEffect.NavigateToResult -> {

                    val randomCategoryId = Random.nextInt(1, Config.JOKE_CATEGORY_COUNT + 1)
                    navController.navigate(

                        ResultJokeRoute.createRoute(
                            categoryId = randomCategoryId,
                            keyword = effect.keyword,
                            entryPoint = ResultJokeEntryPoint.FROM_SEARCH
                        )
                    )
                }
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onEvent = viewModel::sendEvent
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeContract.HomeState,
    onEvent: (HomeContract.HomeEvent) -> Unit
) {
    val scrollState = rememberScrollState()

    if (uiState.isEditNicknameDialogOpen) {
        EditNicknameDialog(
            state = uiState.nicknameDialogState,
            onDismissRequest = { onEvent(HomeContract.HomeEvent.OnNicknameDialogDismiss) },
            onConfirmation = { onEvent(HomeContract.HomeEvent.OnSaveNickname) },
            onNicknameChange = { newNickname ->
                onEvent(HomeContract.HomeEvent.OnNicknameChange(newNickname))
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = Dp.spacing_16)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.home_title_text),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 28.sp,
                    lineHeight = 40.sp
                ),
                modifier = Modifier.padding(top = 52.dp)
            )

            val (imageRes, contentDescRes) = when (uiState.selectedCharacterIndex) {
                GILL_GILL_MON -> R.drawable.home_ggill_gill_mon to R.string.gill_gill_mon
                HEE_HEE_MON -> R.drawable.home_hee_hee_mon to R.string.hee_hee_mon
                KICK_KICK_MON -> R.drawable.home_kick_kick_mon to R.string.kick_kick_mon
                else -> null to null // 기본값 또는 예외 처리
            }

            if (imageRes != null && contentDescRes != null) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = stringResource(contentDescRes),
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            EditableNameChip(
                uiState.nickname,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    onEvent(HomeContract.HomeEvent.OnNicknameEditClick)
                })

            Spacer(modifier = Modifier.height(100.dp))

            CommonButton(stringResource(R.string.home_do_joke_btn_text) , isEnabled = true) {
                onEvent(HomeContract.HomeEvent.OnJokeBtnClicked)
            }

            Spacer(modifier = Modifier.height(Dp.spacing_16))

            uiState.recommendKeyword?.keywords?.let { KeywordCloud(it ,
                onKeywordClick = { keyword ->
                onEvent(HomeContract.HomeEvent.OnTagClicked(keyword))
            }) }
        }

        CommonLoading(uiState.isLoading)
    }
}


@Composable
fun EditableNameChip(
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(BlackAlpha80, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick) // 클릭 가능하게 설정
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = name,
            style = MaterialTheme.typography.bodyMedium
                .copy(color = Color.White, fontSize = 14.sp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_edit_nickname),
            contentDescription = "Edit name",
            tint = Color.White,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun KeywordChip(
    text: String,
    backgroundColor: Color,
    onClick: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(Dp.spacing_16))
            .background(backgroundColor)
            .clickable(onClick = {onClick(text)})
            .padding(top = 10.dp, start = 12.dp, bottom = 8.dp, end = 12.dp)
    ) {
        Text(text = text, color = GrayscaleBlack ,
            style = MaterialTheme.typography.bodyMedium
                .copy(fontSize = 14.sp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordCloud(keywords: List<String> , onKeywordClick: (String) -> Unit) {
    val colors = listOf(
        Color(0xFFF8F5FF),
        Color(0xFFE0F8E7),
        Color(0xFFFFD7C5),
    )
    FlowRow(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dp.spacing_8 ,  alignment = Alignment.CenterHorizontally ),
        verticalArrangement = Arrangement.spacedBy(Dp.spacing_8)
    ) {
        keywords.forEachIndexed { index, keyword ->
            val color = colors[index % colors.size]
            KeywordChip(text = keyword, backgroundColor = color , onClick = onKeywordClick)
        }
    }
}

@Composable
fun EditNicknameDialog(
    state: NicknameDialogState,
    onNicknameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    val maxNicknameLength = 6
    val isConfirmButtonEnabled = state.nickname.isNotBlank() && !state.isError

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dp.spacing_16),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 24.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.edit_character_nickname),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.nickname,
                    onValueChange = {
                        if (it.length <= maxNicknameLength) {
                            onNicknameChange(it)
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = {
                        Text(
                            stringResource(R.string.edit_character_nickname_hint),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Grayscale500)
                        )
                    },
                    singleLine = true,
                    isError = state.isError,
                    trailingIcon = {
                        if (state.nickname.isNotBlank()) {
                            IconButton(onClick = { onNicknameChange("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary, // 보라색
                        unfocusedBorderColor = Grayscale700,
                        errorBorderColor = SemanticError
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.helperText.resolve(LocalContext.current),
                        color = if (state.isError) SemanticError else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W400
                        )
                    )
                    Text(
                        text = "${state.nickname.length}/$maxNicknameLength",
                        color = if (state.isError) SemanticError else Grayscale500,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W500
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Grayscale300
                        )
                    ) {
                        Text(
                            stringResource(R.string.cancel_btn),
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = GrayscaleBlack
                            )
                        )
                    }
                    Button(
                        onClick = onConfirmation,
                        enabled = isConfirmButtonEnabled,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // 보라색
                            disabledContainerColor = Grayscale400
                        )
                    ) {
                        Text(
                            stringResource(R.string.register_btn),
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                                .copy(fontSize = 14.sp, color = Color.White)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    JokeMonTheme {
        Surface {
            val fakeUiState = HomeContract.HomeState(
                isLoading = true,
                nickname = "프리뷰",
                selectedCharacterIndex = 1,
                recommendKeyword = KeywordResponse(keywords = listOf("키워드1", "키워드2", "키워드2"))
            )

            val fakeEditNicknameDialogState = HomeContract.NicknameDialogState()

            fun onEvent(event: HomeContract.HomeEvent) {}

            HomeScreenContent(
                uiState = fakeUiState,
                ::onEvent
            )
        }
    }
}