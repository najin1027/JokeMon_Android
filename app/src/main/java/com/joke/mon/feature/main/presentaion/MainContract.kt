package com.joke.mon.feature.main.presentaion

import com.joke.mon.core.base.BaseContract

object MainContract : BaseContract<MainContract.MainState, MainContract.MainEvent , MainContract.MainEffect>
{
    data class MainState(
        val showReviewDialog: Boolean = false,
        val showExitDialog: Boolean = false
    ) : BaseContract.State


    sealed interface MainEvent : BaseContract.Event {
        object OnBackPressed : MainEvent

        object OnReviewWriteClicked : MainEvent
        object OnReviewCancelClicked : MainEvent
        object OnReviewExitClicked : MainEvent

        object OnExitCancelClicked : MainEvent
        object OnExitConfirmClicked : MainEvent
    }

    sealed interface MainEffect : BaseContract.Effect {
        object ExitApp : MainEffect
        object OpenStoreReview : MainEffect
    }
}