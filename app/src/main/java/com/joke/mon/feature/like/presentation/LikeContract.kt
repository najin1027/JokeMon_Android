package com.joke.mon.feature.like.presentation

import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.util.Config
import com.joke.mon.core.util.UiText
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke

object LikeContract : BaseContract<LikeContract.LikeState , LikeContract.LikeEvent , LikeContract.LikeEffect>
{
    data class LikeState(
        val isLoading : Boolean = false,
        val selectedCategoryId: Int = 0,
        val allJokes: List<LikeJoke> = emptyList(),
        val filteredJokes: List<LikeJoke> = emptyList(),
        val isSortMenuVisible: Boolean = false,
        val sortOption: Config.SortOption = Config.SortOption.LATEST
    ) : BaseContract.State

    sealed interface LikeEvent : BaseContract.Event {

        data object OnBackBtnClicked : LikeEvent
        data class OnItemClicked(val jokeKey: String) : LikeEvent
        data class OnCategorySelected(val categoryId: Int) : LikeEvent
        data class OnLikeToggled(val joke: LikeJoke) : LikeEvent

        data object OnSortMenuOpen : LikeEvent
        data object OnSortMenuDismiss : LikeEvent
        data class OnSortSelected(val option: Config.SortOption) : LikeEvent
    }

    sealed interface LikeEffect : BaseContract.Effect {
        data object NavigateBack : LikeEffect
        data class NavigateToDetail(val jokeKey: String) : LikeEffect
        data class ShowSnackBar(val message: UiText) : LikeEffect
    }
}
