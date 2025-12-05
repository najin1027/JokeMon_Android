package com.joke.mon.feature.search.presentation

import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.util.UiText

object SearchContract : BaseContract<SearchContract.SearchState , SearchContract.SearchEvent , SearchContract.SearchEffect>
{

    data class SearchState(
        val isLoading : Boolean = false,
        val selectedCharacterIndex: Int = 0,
        val selectedCategoryId: Int = 0,
        val isActionBtnEnabled : Boolean = false,
        val keyword : String = ""
    ) : BaseContract.State

    sealed interface SearchEvent : BaseContract.Event {
        data class OnCategorySelected(val categoryId: Int) : SearchEvent

        data class OnKeywordChanged(val keyword: String) : SearchEvent

        data object OnSearchButtonClicked : SearchEvent
    }

    sealed interface SearchEffect : BaseContract.Effect {
        data class NavigateToResult(val categoryId: Int, val keyword: String) : SearchEffect
        data class ShowSnackBar(val message: UiText) : SearchEffect
    }
}