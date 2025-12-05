package com.joke.mon.feature.recent_joke.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joke.mon.R
import com.joke.mon.core.util.Config
import com.joke.mon.core.util.Utils.formatDateTime
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import com.joke.mon.feature.result_joke.presentation.ENTRY_POINT_KEY
import com.joke.mon.feature.result_joke.presentation.JOKE_KEY
import com.joke.mon.feature.result_joke.presentation.ResultJokeEntryPoint
import com.joke.mon.ui.component.CommonAppBar
import com.joke.mon.ui.component.SortMenuDropdown
import com.joke.mon.ui.navigation.ResultJokeRoute
import com.joke.mon.ui.theme.BlackAlpha08
import com.joke.mon.ui.theme.Grayscale600
import com.joke.mon.ui.theme.Grayscale900
import com.joke.mon.ui.theme.JokeMonTheme

@Composable
fun RecentJokeScreen(navController: NavController, viewModel : RecentJokeViewModel = hiltViewModel())
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect {effect ->
            when(effect) {
                is RecentJokeContract.RecentJokeEffect.NavigateToResultJoke -> {
                    navController.navigate(
                        ResultJokeRoute.createRoute(
                            jokeKey = effect.jokeKey,
                            entryPoint = ResultJokeEntryPoint.FROM_RECENT_LIST
                        )
                    )
                }

                RecentJokeContract.RecentJokeEffect.NavigateToBack ->  {
                    navController.popBackStack()
                }
            }
        }
    }

    RecentJokeContent(
        uiState,
        viewModel::sendEvent
    )
}

@Composable
fun RecentJokeContent(
    uiState : RecentJokeContract.RecentJokeState,
    onEvent : (RecentJokeContract.RecentJokeEvent) -> Unit
)
{
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.sortOption) {
        listState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            Box {
                CommonAppBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .statusBarsPadding(),
                    title = stringResource(R.string.recent_created_joke),
                    leftIconPainter = painterResource(R.drawable.ic_back_arrow),
                    onLeftIconClick = {
                        onEvent(RecentJokeContract.RecentJokeEvent.OnBackClicked)
                    },
                    rightIconPainter = painterResource(R.drawable.ic_more),
                    onRightIconClick = {
                        onEvent(RecentJokeContract.RecentJokeEvent.OnSortMenuOpen)
                    }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            end = 16.dp,
                            top = 56.dp)
                ) {
                    SortMenuDropdown(
                        expanded = uiState.isSortMenuVisible,
                        sortOption = uiState.sortOption,
                        onDismiss = {
                            onEvent(RecentJokeContract.RecentJokeEvent.OnSortMenuDismiss)
                        },
                        onSelected = { selected ->
                            onEvent(
                                RecentJokeContract.RecentJokeEvent.OnSortSelected(selected)
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if(uiState.recentJokes.isEmpty())
            {
                Box(modifier =
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(bottom = 80.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(R.drawable.ic_history_large),
                             contentDescription = "history"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.no_recently_joke_list),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp,
                                color = Grayscale900)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.create_funny_jokes),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Grayscale600)
                        )
                    }
                }
            }
            else
            {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    state = listState
                ) {
                    items(uiState.recentJokes , key = {it.id}) { joke ->
                        RecentJokeItem(
                            joke = joke,
                            onItemClick = {
                                onEvent(RecentJokeContract.RecentJokeEvent.OnJokeClicked(joke.key))}
                        )
                    }
                }

            }
        }
    }
}
@Composable
fun RecentJokeItem(
    joke: RecentJoke,
    onItemClick: (RecentJoke) -> Unit,
    modifier: Modifier = Modifier
) {

    var isOverflow by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(joke) }
            .padding(vertical = 12.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = formatDateTime(joke.createdAt),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Grayscale600
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            ,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = joke.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                onTextLayout = { textLayout ->
                    // 3줄 초과 여부 체크
                    isOverflow = textLayout.hasVisualOverflow
                }
            )

//            if (isOverflow) {
//                Text(
//                    text = "더보기",
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontSize = 16.sp,
//                        color = GrayscaleBlack,
//                    ),
//                    modifier = Modifier
//                        .padding(start = 4.dp)
//                        .clickable { onItemClick(joke) }
//                )
//            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = BlackAlpha08 , thickness = 1.dp)
    }
}

@Preview
@Composable
fun RecentJokeScreenPreview()
{
    val recentJokes = mutableListOf<RecentJoke>()
    recentJokes.add(
        RecentJoke(
            key = "dwdw",
            content = "회의 중에 졸다가 지적받은 동료가 이렇게 말했어요. " +
                    "“죄송합니다, 제가 회의 내용을 꿈속에서 더 잘 이해하고 있었네요!”" +
                    "죄송합니다, 제가 회의 내용을 꿈속에서 더 잘 이해하고 있었네요!”" +
                    "죄송합니다, 제가 회의 내용을 꿈속에서 더 잘 이해하고 있었네요!”" +
                    "",
            categoryId = Config.JOKE_CATEGORY_1,
            createdAt = "2025-11-11 11:11:11",
            keyword = "회의",
            isSaved = false
        )
    )

    val uiState = RecentJokeContract.RecentJokeState(recentJokes = recentJokes)

    JokeMonTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            RecentJokeContent(uiState , {

            })
        }
    }
}