package com.joke.mon.feature.like.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.joke.mon.R
import com.joke.mon.core.data.model.JokeCategory
import com.joke.mon.core.util.Utils.getJokeBackgroundColor
import com.joke.mon.core.util.Utils.getJokeChipBackgroundColor
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import com.joke.mon.feature.result_joke.presentation.CATEGORY_ID_KEY
import com.joke.mon.feature.result_joke.presentation.ENTRY_POINT_KEY
import com.joke.mon.feature.result_joke.presentation.JOKE_KEY
import com.joke.mon.feature.result_joke.presentation.KEYWORD_KEY
import com.joke.mon.feature.result_joke.presentation.ResultJokeEntryPoint
import com.joke.mon.ui.component.CommonAppBar
import com.joke.mon.ui.component.SortMenuDropdown
import com.joke.mon.ui.navigation.AppRoute
import com.joke.mon.ui.navigation.ResultJokeRoute
import com.joke.mon.ui.theme.BlackAlpha16
import com.joke.mon.ui.theme.Grayscale300
import com.joke.mon.ui.theme.Grayscale600
import com.joke.mon.ui.theme.Grayscale700
import com.joke.mon.ui.theme.Grayscale900
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.JokeMonTheme
import com.joke.mon.ui.theme.SemanticLike


private val likeCategories = listOf(
    JokeCategory(id = 0, text = "전체"),
    JokeCategory(id = 1 , text = "요즘유행", emoji = "🙊"),
    JokeCategory(id = 2 ,text = "아재감성", emoji = "🧔🏻‍♂️"),
    JokeCategory(id = 3 , text = "어이없잼", emoji = "😎")
)



@Composable
fun LikeScreen(navController: NavController, viewModel: LikeViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LikeScreenContent(uiState , viewModel::sendEvent)


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                LikeContract.LikeEffect.NavigateBack ->  {
                     navController.navigate(AppRoute.MAIN) {
                        popUpTo(AppRoute.MAIN) {
                            inclusive = true
                        }
                    }
                }
                is LikeContract.LikeEffect.NavigateToDetail ->  {
                    navController.navigate(
                        ResultJokeRoute.createRoute(
                            jokeKey = effect.jokeKey,
                            entryPoint = ResultJokeEntryPoint.FROM_LIKE_LIST
                        )
                    )
                }
                is LikeContract.LikeEffect.ShowSnackBar ->  {

                }
            }
        }
    }
}

@Composable
fun LikeScreenContent(
    uiState : LikeContract.LikeState,
    onEvent : (LikeContract.LikeEvent) -> Unit
) {

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.sortOption , uiState.selectedCategoryId) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box {
            CommonAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(R.string.liked_joke),
                leftIconPainter = painterResource(R.drawable.ic_back_arrow),
                onLeftIconClick = {
                    onEvent(LikeContract.LikeEvent.OnBackBtnClicked)
                },
                rightIconPainter = painterResource(R.drawable.ic_more),
                onRightIconClick = {
                    onEvent(LikeContract.LikeEvent.OnSortMenuOpen)
                }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        end = 16.dp,
                        top = 56.dp
                    )
            ) {
                SortMenuDropdown(
                    expanded = uiState.isSortMenuVisible,
                    sortOption = uiState.sortOption,
                    onDismiss = { onEvent(LikeContract.LikeEvent.OnSortMenuDismiss) },
                    onSelected = { selected ->
                        onEvent(LikeContract.LikeEvent.OnSortSelected(selected))
                    }
                )
            }

        }

        CategoryChipList(
            categories = likeCategories,
            selectedCategoryId = uiState.selectedCategoryId,
            onCategorySelected = { categoryId ->
                onEvent(LikeContract.LikeEvent.OnCategorySelected(categoryId))
            }
        )

        if(uiState.filteredJokes.isEmpty())
        {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(R.drawable.ic_heart_large) , contentDescription = "Like")

                Spacer(Modifier.height(8.dp))
                Text(
                    text= stringResource(R.string.no_liked_joke_list),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = W400,
                        color = Grayscale900
                    )
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.recommend_like_joke),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp , color = Grayscale600)
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = listState
            ) {
                items(uiState.filteredJokes, key = { it.id }) { joke ->
                    val category = likeCategories.find { it.id == joke.categoryId }
                    JokeCardItem(
                        joke = joke,
                        category = category,
                        onLikeToggled = { onEvent(LikeContract.LikeEvent.OnLikeToggled(joke)) },
                        onJokeClicked = {onEvent(LikeContract.LikeEvent.OnItemClicked(joke.key))}
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChipList(
    categories: List<JokeCategory>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryChip(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: JokeCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) GrayscaleBlack else Color.White,
        label = "Chip Background Color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Grayscale700,
        label = "Chip Content Color"
    )
    Box(
        modifier = Modifier
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = Grayscale300,
                shape = RoundedCornerShape(50.dp)
            )
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(top = 8.dp, bottom = 6.dp, start = 12.dp, end = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = category.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp , color = contentColor , fontWeight = if(isSelected) FontWeight.W700 else FontWeight.W400
               )
            )
            if (category.emoji != null) {
                Text(
                    text = category.emoji,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp , color = contentColor , fontWeight = if(isSelected) FontWeight.W700 else FontWeight.W400
                    )
                )
            }
        }
    }
}

@Composable
fun JokeCardItem(
    joke: LikeJoke, // Joke -> LikeJoke
    category: JokeCategory?,
    onLikeToggled: () -> Unit,
    onJokeClicked: () -> Unit
) {
    val backgroundColor = getJokeBackgroundColor(joke.categoryId)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onJokeClicked
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LikeButton(
                onClick = onLikeToggled,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                if (category != null) {
                    JokeItemCategoryChip(category = category)
                }
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = joke.content,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp , fontWeight = W400),
                    color = GrayscaleBlack,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
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
fun LikeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_circle_like_fill_large),
            tint = Color.Unspecified,
            contentDescription = "Like",
        )
    }
}



@Preview(showBackground = true)
@Composable
fun LikeScreenPreview() {

    val tempJokes = listOf(
        LikeJoke(
            id = 1,
            key = "key1",
            content = "회의 중에 졸다가 지적받은 동료가 이렇게 말했어요. “죄송합니다, 제가 회의 내용을 꿈속에서 더 잘 이해하고 있었네요!”",
            categoryId = 1, // 1: 야재개그
            createdAt = "2025-10-20",
            keyword = "회의",
            isSaved = true
        ),
        LikeJoke(
            id = 2,
            key = "key2",
            content = "텍스트 내용이 세 줄을 넘어가면 이렇게 보여집니다. 텍스트 내용이 세 줄을 넘어가면 이렇게 보여집니다.. 텍스트 내용이 세 줄을 넘으면...더보기",
            categoryId = 2, // 2: 단순 농담
            createdAt = "2025-10-19",
            keyword = "텍스트",
            isSaved = true
        ),
        LikeJoke(
            id = 3,
            key = "key3",
            content = "왕이 궁궐에 가기 싫으면? 궁시렁궁시렁~",
            categoryId = 3, // 3: 말장난
            createdAt = "2025-10-18",
            keyword = "왕",
            isSaved = false
        )
    )

    val mockState = LikeContract.LikeState(
        selectedCategoryId = 0,
        allJokes = tempJokes,
        filteredJokes = tempJokes
    )

    JokeMonTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()) {
            LikeScreenContent(
                uiState = mockState,
                onEvent = {}
            )
        }
    }
}