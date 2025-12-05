package com.joke.mon.feature.result_joke.presentation

import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.data.model.JokeCategory
import com.joke.mon.core.data.model.VoiceConfig

object ResultJokeContract : BaseContract<ResultJokeContract.ResultJokeState ,  ResultJokeContract.ResultJokeEvent , ResultJokeContract.ResultJokeEffect>
{

    data class ResultJokeState(
        val isLoading: Boolean = true,
        val isVoiceLoading: Boolean = false,
        val jokeKey : String = "",
        val keyword : String = "",
        val jokeContent: String = "",
        val jokeCategoryId: Int = 0,
        val isLiked: Boolean = false,
        val createdAt : String = "",
        val category: JokeCategory? = null,
        val selectedCharacterIndex: Int = 0,
        val isJokePlaying: Boolean = false,
        val errorMessage: String? = null,
        val voiceConfig: VoiceConfig = VoiceConfig(),
        val isVoiceSheetVisible: Boolean = false
    )  : BaseContract.State


    sealed interface ResultJokeEvent : BaseContract.Event
    {
        data object OnBackClicked : ResultJokeEvent
        data object OnHomeClicked : ResultJokeEvent

        data object OnLikeToggled : ResultJokeEvent
        data class OnCopyClicked(val content: String) : ResultJokeEvent
        data object OnRegenerateClicked : ResultJokeEvent

        data object  OnPlayJokeClicked : ResultJokeEvent

        data object OnOpenVoiceSettings : ResultJokeEvent
        data object OnCloseVoiceSettings : ResultJokeEvent

        data class OnApplyVoiceSettings(val config: VoiceConfig) : ResultJokeEvent

        data class OnPreviewVoice(val config: VoiceConfig) : ResultJokeEvent

        data object OnShareClicked : ResultJokeEvent

        data object OnStopVoice : ResultJokeEvent
    }

    sealed interface ResultJokeEffect : BaseContract.Effect {
        data object NavigateBack : ResultJokeEffect
        data object NavigateToHome : ResultJokeEffect
        data class CopyToClipboard(val text: String) : ResultJokeEffect
        data class ShareJoke(val text: String) : ResultJokeEffect
        data class ShowSnackBar(val message: String) : ResultJokeEffect
    }
}