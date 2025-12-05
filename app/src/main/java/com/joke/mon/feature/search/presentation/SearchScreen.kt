package com.joke.mon.feature.search.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joke.mon.R
import com.joke.mon.core.util.Config.GILL_GILL_MON
import com.joke.mon.core.util.Config.HEE_HEE_MON
import com.joke.mon.core.util.Config.KICK_KICK_MON
import androidx.compose.foundation.layout.Spacer
import com.joke.mon.ui.theme.JokeMonTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.joke.mon.core.data.model.JokeCategory
import com.joke.mon.core.util.Config
import com.joke.mon.feature.result_joke.presentation.CATEGORY_ID_KEY
import com.joke.mon.feature.result_joke.presentation.ENTRY_POINT_KEY
import com.joke.mon.feature.result_joke.presentation.KEYWORD_KEY
import com.joke.mon.feature.result_joke.presentation.ResultJokeEntryPoint
import com.joke.mon.ui.component.CommonButton
import com.joke.mon.ui.navigation.ResultJokeRoute
import com.joke.mon.ui.theme.BlackAlpha16
import com.joke.mon.ui.theme.Grayscale100
import com.joke.mon.ui.theme.Grayscale600
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.Primary300

private val jokeCategories = listOf(
    JokeCategory(id = 0 ,text = "아무거나", emoji = "🎲",), // 예시 아이콘
    JokeCategory(id = Config.JOKE_CATEGORY_1 , text = "요즘유행", emoji = "🙊"),
    JokeCategory(id = Config.JOKE_CATEGORY_2 ,text = "아재감성", emoji = "🧔🏻‍♂️"),
    JokeCategory(id = Config.JOKE_CATEGORY_3 , text = "어이없잼", emoji = "😎")
)

@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchContract.SearchEffect.NavigateToResult -> {
                    val finalCategoryId = if (effect.categoryId == 0) {
                        val validCategoryIds = listOf(
                            Config.JOKE_CATEGORY_1,
                            Config.JOKE_CATEGORY_2,
                            Config.JOKE_CATEGORY_3
                        )
                        validCategoryIds.random()
                    } else {
                        effect.categoryId
                    }

                    navController.navigate(
                        ResultJokeRoute.createRoute(
                            categoryId = finalCategoryId,
                            keyword = effect.keyword,
                            entryPoint = ResultJokeEntryPoint.FROM_SEARCH
                        )
                    )
                }

                is SearchContract.SearchEffect.ShowSnackBar -> {

                }
            }
        }
    }

    SearchScreenContent(
        uiState = uiState,
        onEvent = viewModel::sendEvent
    )
}

@Composable
fun SearchScreenContent(
    uiState: SearchContract.SearchState,
    onEvent: (SearchContract.SearchEvent) -> Unit,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            Text(
                text = stringResource(R.string.bespoke_joke),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
            )

            val (imageRes, contentDescRes) = when (uiState.selectedCharacterIndex) {
                GILL_GILL_MON -> R.drawable.search_ggil_ggill_mon to R.string.gill_gill_mon
                HEE_HEE_MON -> R.drawable.search_hee_hee_mon to R.string.hee_hee_mon
                KICK_KICK_MON -> R.drawable.search_kick_kick_mon to R.string.kick_kick_mon
                else -> null to null // 기본값 또는 예외 처리
            }

            if (imageRes != null && contentDescRes != null) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = stringResource(contentDescRes),
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .size(width = 140.dp, height = 140.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(12.dp)) // 여백 추가

            JokeCategoryRow(
                categories = jokeCategories,
                selectedCategoryIndex = uiState.selectedCategoryId,
                onCategorySelected = { index ->
                    onEvent(SearchContract.SearchEvent.OnCategorySelected(index))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KeywordTextField(
                value = uiState.keyword,
                onValueChange = { newKeyword ->
                    onEvent(SearchContract.SearchEvent.OnKeywordChanged(newKeyword))
                },
                onSearch = {
                    onEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
                }
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CommonButton(stringResource(R.string.search_do_joke_btn_text) ,
                isEnabled = uiState.isActionBtnEnabled,
            ) {
                onEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
            }
        }

    }
}

@Composable
fun JokeCategoryRow(
    categories: List<JokeCategory>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.spacedBy(8.dp),

    ) {
        categories.forEachIndexed { index, category ->
            JokeCategoryChip(
                category = category,
                isSelected = index == selectedCategoryIndex,
                modifier = Modifier.weight(1f),
                onClick = { onCategorySelected(index) }
            )
        }
    }
}

@Composable
fun JokeCategoryChip(
    category: JokeCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else Color.White,
        label = "background color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else GrayscaleBlack,
        label = "content color"
    )
    val shadowColor = if (isSelected) MaterialTheme.colorScheme.primary else BlackAlpha16

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 24.dp else 16.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = shadowColor
            )
            .border(
                width = 1.dp,
                color = if(isSelected) Primary300 else Grayscale100,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            category.emoji?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge.copy(color=contentColor)) }

            Spacer(modifier = Modifier.height(10.dp))

            if(isSelected) {
                Text(
                    text = category.text,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp ,
                        fontWeight = W700)
                )
            }
            else {
                Text(
                    text = category.text,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
fun KeywordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
        singleLine = true,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 0.dp)
            .wrapContentHeight()
            .border(
                width = 2.dp,
                color = Primary300,
                shape = RoundedCornerShape(16.dp)
            )

        ,
        placeholder = {
            Text(text = stringResource(R.string.keyword_text_filed_hint) ,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp , color = Grayscale600))
        },

        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Image(
                        painter = painterResource(R.drawable.ic_remove),
                        contentDescription = "Clear text"
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,

            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            cursorColor = MaterialTheme.colorScheme.primary,
            
            focusedTextColor = GrayscaleBlack
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                if(value.isNotBlank()) {
                    onSearch()
                    focusManager.clearFocus()
                }
            }
        )
    )
}

@Preview
@Composable
fun SearchScreenPreview() {
    JokeMonTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            val fakeUiState = SearchContract.SearchState(
                isLoading = true,
                selectedCharacterIndex = 1,
            )


            SearchScreenContent(
                uiState = fakeUiState,
                onEvent = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KeywordTextFieldPreview() {
    Box(modifier = Modifier.padding(16.dp)){
        KeywordTextField(
            value = "미리보기 텍스트",
            onValueChange = {},
            {}
            )
    }

}