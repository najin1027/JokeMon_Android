package com.joke.mon.feature.recent_joke.presentation

import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.util.Config
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke

object RecentJokeContract : BaseContract<RecentJokeContract.RecentJokeState , RecentJokeContract.RecentJokeEvent , RecentJokeContract.RecentJokeEffect>
{
    data class RecentJokeState(
        val isLoading : Boolean = false,
        val isSortMenuVisible: Boolean = false,
        val sortOption: Config.SortOption = Config.SortOption.LATEST,
        val recentJokes : List<RecentJoke> = emptyList()
    ) : BaseContract.State


    sealed interface RecentJokeEvent : BaseContract.Event
    {
        data class OnJokeClicked(val jokeKey : String) : RecentJokeEvent
        data object OnBackClicked : RecentJokeEvent

        data object OnSortMenuOpen : RecentJokeEvent
        data object OnSortMenuDismiss : RecentJokeEvent
        data class OnSortSelected(val option: Config.SortOption) : RecentJokeEvent
    }

    sealed interface RecentJokeEffect : BaseContract.Effect
    {
        data class NavigateToResultJoke(val jokeKey :  String) : RecentJokeEffect
        data object NavigateToBack :  RecentJokeEffect
    }
}