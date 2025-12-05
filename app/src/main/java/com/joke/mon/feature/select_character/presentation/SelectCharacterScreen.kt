package com.joke.mon.feature.select_character.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.joke.mon.R
import com.joke.mon.core.data.model.CharacterData
import com.joke.mon.core.data.model.Characters
import com.joke.mon.core.data.model.SelectedCharacter
import com.joke.mon.core.util.Config
import com.joke.mon.ui.component.CommonAppBar
import com.joke.mon.ui.component.CommonButton
import com.joke.mon.ui.component.CommonSnackBar
import com.joke.mon.ui.theme.BlackAlpha64
import com.joke.mon.ui.theme.BlackAlpha80
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.JokeMonTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun SelectCharacterScreen(navController: NavController,
                          viewModel : SelectCharacterViewModel = hiltViewModel())
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var snackBarJob by remember { mutableStateOf<Job?>(null) }


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                SelectCharacterContract.SelectCharacterEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                is SelectCharacterContract.SelectCharacterEffect.SaveCharacterNavigateBack -> {
                    snackBarJob?.cancel()
                    snackBarJob = scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(effect.message)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
    
    SelectCharacterScreenContent(
        uiState,
        snackBarHostState = snackBarHostState,
        viewModel::sendEvent
    )
}

@Composable
fun SelectCharacterScreenContent(
    uiState : SelectCharacterContract.SelectCharacterState,
    snackBarHostState: SnackbarHostState,
    onEvent : (SelectCharacterContract.SelectCharacterEvent) -> Unit
)
{
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
                title = stringResource(R.string.select_character),
                leftIconPainter = painterResource(R.drawable.ic_back_arrow),
                onLeftIconClick = {
                    onEvent(SelectCharacterContract.SelectCharacterEvent.OnBackClicked)
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.select_character_msg1),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 28.sp )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.select_character_msg2),
                style = MaterialTheme.typography.bodyMedium.copy(color = BlackAlpha80)
            )

            SelectedCharacterSection(
                selectedCharacter = uiState.selectedCharacter
            )
            Spacer(modifier = Modifier.height(38.dp))

            CharacterSelectorRow(
                characters = Characters,
                selectedIndex = uiState.selectedCharacterIndex,
                onCharacterSelected = { index ->
                    onEvent(SelectCharacterContract.SelectCharacterEvent.OnCharacterSelected(index))
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            CommonButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                buttonText = stringResource(R.string.select_character_button_text),
                isEnabled = true
            ) {
                onEvent(SelectCharacterContract.SelectCharacterEvent.OnSaveCharacterClicked)
            }
        }
    }
}

@Composable
fun SelectedCharacterSection(
    selectedCharacter: CharacterData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(260.dp)
                .background(
                    color = selectedCharacter.backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 36.dp), // 원 바닥과의 여백
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedCharacter.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedCharacter.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = BlackAlpha64
                    )
                )
            }
        }

        Image(
            painter = painterResource(id = selectedCharacter.imageRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(260.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun CharacterSelectorRow(
    characters: List<CharacterData>,
    selectedIndex: Int,
    onCharacterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        characters.forEachIndexed { index, character ->
            CharacterAvatar(
                character = character,
                isSelected = index == selectedIndex,
                onClick = { onCharacterSelected(index) }
            )
        }
    }
}

@Composable
fun CharacterAvatar(
    character: CharacterData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF5B1BD0) else Color.Transparent,
        label = "borderColor"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        label = "borderWidth"
    )

    Column(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(character.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = character.imageRes),
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.2f)
                    .offset(y = (14).dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = character.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = if (isSelected) GrayscaleBlack else Color.Gray
            )
        )
    }
}

@Preview
@Composable
fun SelectCharacterScreenPreview() {
    JokeMonTheme {
        val snackBarHostState = SnackbarHostState()
        val uiState = SelectCharacterContract.SelectCharacterState()
        SelectCharacterScreenContent(
            uiState,
            snackBarHostState,
            {}
        )
    }
}

@Preview
@Composable
fun SelectedCharacterSectionPreview() {

    val character = CharacterData(
        id = Config.HEE_HEE_MON,
        name = "히히몬",
        description = "다정하고 따뜻한 히히몬",
        backgroundColor = Color(0xFFFEF0D6),
        imageRes = R.drawable.hee_hee_mon //
    )

    JokeMonTheme {
        Box(Modifier.fillMaxSize()) {

        SelectedCharacterSection(
            character
        )

        }
    }
}