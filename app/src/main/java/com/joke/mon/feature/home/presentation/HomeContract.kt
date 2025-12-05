package com.joke.mon.feature.home.presentation

import com.joke.mon.R
import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.util.UiText
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse

object HomeContract : BaseContract<
        HomeContract.HomeState,
        HomeContract.HomeEvent,
        HomeContract.HomeEffect> {

    data class HomeState(
        val isLoading: Boolean = false,
        val recommendKeyword: KeywordResponse? = null,
        val selectedCharacterIndex: Int = 0,
        val nickname: String = "",
        val isEditNicknameDialogOpen: Boolean = false,
        val nicknameDialogState: NicknameDialogState = NicknameDialogState(), // 포함
        val error: String? = null
    ) : BaseContract.State


    data class NicknameDialogState(
        val nickname: String = "",
        val isError: Boolean = false,
        val helperText: UiText = UiText.FromRes(R.string.nickname_edit_dialog_guide_msg)
    )

    sealed interface HomeEvent : BaseContract.Event {
        object OnNicknameEditClick : HomeEvent
        object OnNicknameDialogDismiss : HomeEvent
        object OnSaveNickname : HomeEvent

        object OnJokeBtnClicked : HomeEvent
        data class OnNicknameChange(val nickname: String) : HomeEvent
        data class OnTagClicked(val keyword: String) : HomeEvent

    }

    sealed interface HomeEffect : BaseContract.Effect {
        data class NavigateToResult(val keyword: String) : HomeEffect
        data class ShowSnackBar(val message: UiText) : HomeEffect
    }
}