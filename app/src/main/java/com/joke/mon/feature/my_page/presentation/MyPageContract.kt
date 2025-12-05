package com.joke.mon.feature.my_page.presentation

import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.util.UiText

object MyPageContract : BaseContract<MyPageContract.MyPageState , MyPageContract.MyPageEvent , MyPageContract.MyPageEffect >
{
    data class MyPageState (
        val isLoading : Boolean = false,
    ): BaseContract.State

    sealed interface MyPageEvent : BaseContract.Event {
        data object OnLikedJokeClicked : MyPageEvent
        data object OnRecentJokeClicked : MyPageEvent
        data object OnSelectCharacterClicked : MyPageEvent
        data object OnNotificationSettingClicked : MyPageEvent
        data object OnContactUsClicked : MyPageEvent

    }

    sealed interface MyPageEffect : BaseContract.Effect {
        data object NavigateToLikedJokes : MyPageEffect
        data object NavigateToRecentJokes : MyPageEffect
        data object NavigateToNotificationSetting : MyPageEffect
        data object OpenContactEmail : MyPageEffect
        data object NavigateSelectCharacterScreen : MyPageEffect
        data class ShowSnackBar(val message: UiText) : MyPageEffect
    }

}