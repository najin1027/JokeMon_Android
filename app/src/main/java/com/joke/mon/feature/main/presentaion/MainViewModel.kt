package com.joke.mon.feature.main.presentaion

import androidx.lifecycle.viewModelScope
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.data.repository.SharePreferenceRepository
import com.joke.mon.core.util.Config.REVIEW_DIALOG_INTERVAL_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharePreferenceRepository: SharePreferenceRepository
) : BaseViewModel<MainContract.MainState , MainContract.MainEvent , MainContract.MainEffect>()
{
    private var lastReviewDialogTime: Long = 0L

    init {
        viewModelScope.launch {
            lastReviewDialogTime = sharePreferenceRepository.getLastReviewDialogTime()
        }
    }

    override fun createInitialState(): MainContract.MainState  = MainContract.MainState()

    override fun handleEvent(event: MainContract.MainEvent) {
        when (event) {
            MainContract.MainEvent.OnBackPressed -> handleBackPressed()

            MainContract.MainEvent.OnReviewWriteClicked -> {
                sendEffect(MainContract.MainEffect.OpenStoreReview)
                updateState { copy(showReviewDialog = false) }
            }
            MainContract.MainEvent.OnReviewCancelClicked -> {
                updateState { copy(showReviewDialog = false) }
            }
            MainContract.MainEvent.OnReviewExitClicked -> {
                updateState { copy(showReviewDialog = false) }
                sendEffect(MainContract.MainEffect.ExitApp)
            }

            MainContract.MainEvent.OnExitCancelClicked -> {
                updateState { copy(showExitDialog = false) }
            }
            MainContract.MainEvent.OnExitConfirmClicked -> {
                updateState { copy(showExitDialog = false) }
                sendEffect(MainContract.MainEffect.ExitApp)
            }
        }
    }

    private fun handleBackPressed() {
        val now = System.currentTimeMillis()
        val shouldShowReview =
            lastReviewDialogTime == 0L || (now - lastReviewDialogTime) > REVIEW_DIALOG_INTERVAL_MILLIS

        if (shouldShowReview) {
            updateState { copy(showReviewDialog = true, showExitDialog = false) }
            lastReviewDialogTime = now
            viewModelScope.launch {
                sharePreferenceRepository.saveLastReviewDialogTime(now)
            }
        } else {
            updateState { copy(showReviewDialog = false, showExitDialog = true) }
        }
    }

}