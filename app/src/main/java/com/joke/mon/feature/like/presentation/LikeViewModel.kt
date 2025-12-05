package com.joke.mon.feature.like.presentation

import androidx.lifecycle.viewModelScope
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.util.Config
import com.joke.mon.feature.like.data.repository.LikeRepository
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val likeRepository: LikeRepository
) : BaseViewModel<LikeContract.LikeState , LikeContract.LikeEvent , LikeContract.LikeEffect>()
{

    init {
        loadLikedJokes()
    }

    override fun createInitialState(): LikeContract.LikeState {
        return LikeContract.LikeState()
    }

    override fun handleEvent(event: LikeContract.LikeEvent) {
        when (event) {
            is LikeContract.LikeEvent.OnItemClicked -> {
                sendEffect(LikeContract.LikeEffect.NavigateToDetail(event.jokeKey))
            }
            is LikeContract.LikeEvent.OnCategorySelected -> {
                updateState {
                    val newCategoryId = event.categoryId
                    copy(
                        selectedCategoryId = newCategoryId,
                        filteredJokes = filterAndSortJokes(
                            jokes = allJokes,
                            categoryId = newCategoryId,
                            sortOption = sortOption
                        )
                    )
                }
            }

            is LikeContract.LikeEvent.OnLikeToggled -> {
                viewModelScope.launch {
                    likeRepository.deleteJokeByKey(event.joke.key)
                }
            }

            LikeContract.LikeEvent.OnBackBtnClicked -> {
                sendEffect(LikeContract.LikeEffect.NavigateBack)
            }

            LikeContract.LikeEvent.OnSortMenuDismiss ->  {
                updateState { copy(isSortMenuVisible = false) }
            }
            LikeContract.LikeEvent.OnSortMenuOpen -> {
                updateState { copy(isSortMenuVisible = true) }
            }
            is LikeContract.LikeEvent.OnSortSelected ->  {
                updateState {
                    val option = event.option
                    copy(
                        sortOption = option,
                        isSortMenuVisible = false,
                        filteredJokes = filterAndSortJokes(
                            jokes = allJokes,
                            categoryId = selectedCategoryId,
                            sortOption = option
                        )
                    )
                }
            }
        }
    }


    private fun loadLikedJokes() {
        likeRepository.getLikedJokesFlow()
            .onEach { jokes ->
                updateState {
                    copy(
                        allJokes = jokes,
                        filteredJokes = filterAndSortJokes(
                            jokes = jokes,
                            categoryId = this.selectedCategoryId,
                            sortOption = this.sortOption
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun filterAndSortJokes(jokes: List<LikeJoke>, categoryId: Int, sortOption: Config.SortOption): List<LikeJoke> {
        val filtered = if (categoryId == 0)
        {
            jokes
        }
        else
        {
            jokes.filter { it.categoryId == categoryId }
        }

        return when (sortOption) {
            Config.SortOption.LATEST ->  filtered.sortedByDescending { it.createdAt }
            Config.SortOption.OLDEST ->filtered.sortedBy { it.createdAt }
        }
    }

}