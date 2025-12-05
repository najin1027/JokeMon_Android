package com.joke.mon.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.data.repository.SharePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sharePreferenceRepository: SharePreferenceRepository
) : BaseViewModel<SearchContract.SearchState , SearchContract.SearchEvent , SearchContract.SearchEffect>()
{
    init {
        observeSelectedCharacterIndex()
    }

    override fun createInitialState(): SearchContract.SearchState {
        return SearchContract.SearchState()
    }

    override fun handleEvent(event: SearchContract.SearchEvent) {
        when(event) {

            is SearchContract.SearchEvent.OnKeywordChanged -> {
                updateState {
                    copy(
                        keyword = event.keyword,
                        isActionBtnEnabled = event.keyword.isNotBlank()
                    )
                }
            }

            is SearchContract.SearchEvent.OnCategorySelected -> {
                updateState { copy(selectedCategoryId = event.categoryId) }
            }

            SearchContract.SearchEvent.OnSearchButtonClicked -> {
                val keyword = uiState.value.keyword.trim()
                if (keyword.isBlank()) return

                sendEffect(
                    SearchContract.SearchEffect.NavigateToResult(
                        categoryId = uiState.value.selectedCategoryId,
                        keyword = keyword
                    )
                )
            }
        }
    }

    private fun observeSelectedCharacterIndex() {
        viewModelScope.launch {
            sharePreferenceRepository
                .observeCurrentSelectedCharacterIndex()
                .distinctUntilChanged()
                .collect { index ->
                    updateState { copy(selectedCharacterIndex = index) }
                }
        }
    }

}