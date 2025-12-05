package com.joke.mon.feature.recent_joke.presentation

import androidx.lifecycle.viewModelScope
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.util.Config
import com.joke.mon.feature.recent_joke.data.repository.RecentJokeRepository
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import com.joke.mon.feature.recent_joke.presentation.RecentJokeContract.RecentJokeEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentJokeViewModel @Inject constructor(
    private val recentJokeRepository: RecentJokeRepository
) : BaseViewModel<RecentJokeContract.RecentJokeState , RecentJokeContract.RecentJokeEvent , RecentJokeContract.RecentJokeEffect>()
{
    init {
        getRecentJokes()
    }
    override fun createInitialState(): RecentJokeContract.RecentJokeState = RecentJokeContract.RecentJokeState()
    override fun handleEvent(event: RecentJokeContract.RecentJokeEvent) {
        when(event) {
            is RecentJokeContract.RecentJokeEvent.OnJokeClicked -> {
                sendEffect(NavigateToResultJoke(event.jokeKey))
            }

            RecentJokeContract.RecentJokeEvent.OnBackClicked -> {
                sendEffect(NavigateToBack)
            }

            RecentJokeContract.RecentJokeEvent.OnSortMenuDismiss ->  {
                updateState { copy(isSortMenuVisible = false) }
            }
            RecentJokeContract.RecentJokeEvent.OnSortMenuOpen -> {
                updateState { copy(isSortMenuVisible = true) }
            }
            is RecentJokeContract.RecentJokeEvent.OnSortSelected ->  {
                val option = event.option
                val current = uiState.value.recentJokes

                updateState {
                    copy(
                        sortOption = option,
                        isSortMenuVisible = false,
                        recentJokes = sortJokes(current, option)
                    )
                }
            }
        }
    }



    private fun getRecentJokes() {
        viewModelScope.launch {
            recentJokeRepository.getRecentJokes().collect { jokes ->
                updateState {
                    copy(
                        recentJokes = sortJokes(jokes, sortOption)
                    )
                }
            }
        }
    }

    private fun sortJokes(
        jokes: List<RecentJoke>,
        sortOption: Config.SortOption
    ): List<RecentJoke> {
        return when (sortOption) {
            Config.SortOption.LATEST -> jokes.sortedByDescending { it.createdAt }
            Config.SortOption.OLDEST -> jokes.sortedBy { it.createdAt }
        }
    }

}