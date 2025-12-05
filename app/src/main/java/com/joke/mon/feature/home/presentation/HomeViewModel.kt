package com.joke.mon.feature.home.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.joke.mon.R
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.data.repository.SharePreferenceRepository
import com.joke.mon.core.util.Config
import com.joke.mon.core.util.Resource
import com.joke.mon.core.util.UiText
import com.joke.mon.feature.home.data.repository.HomeRepository
import com.joke.mon.feature.home.presentation.HomeContract.HomeEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val homeRepository: HomeRepository,
    private val sharePreferenceRepository: SharePreferenceRepository
) : BaseViewModel<HomeContract.HomeState, HomeContract.HomeEvent, HomeContract.HomeEffect>() {

    override fun createInitialState(): HomeContract.HomeState {
        return HomeContract.HomeState()
    }
    private var keywordJob: Job? = null

    init {
        loadHomeData()
        fetchRecommendKeyword(false)
        observeSelectedCharacterIndex()
    }

    override fun handleEvent(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.OnNicknameEditClick -> {
                openNicknameDialog()
            }
            is HomeContract.HomeEvent.OnNicknameDialogDismiss -> {
                updateState { copy(isEditNicknameDialogOpen = false) }
            }
            is HomeContract.HomeEvent.OnNicknameChange -> {
                onNicknameChange(event.nickname)
            }
            is HomeContract.HomeEvent.OnSaveNickname -> {
                onSaveNickName()
            }
            is HomeContract.HomeEvent.OnTagClicked -> {
                sendEffect(NavigateToResult(event.keyword))
            }

            HomeContract.HomeEvent.OnJokeBtnClicked -> {
                sendEffect(NavigateToResult(keyword = context.getString(R.string.home_do_joke_btn_text)))
            }
        }
    }

    private fun observeSelectedCharacterIndex() {
        viewModelScope.launch {
            sharePreferenceRepository
                .observeCurrentSelectedCharacterIndex()
                .distinctUntilChanged()
                .collect { index ->
                    val nickname = resolveNicknameFor(index)

                    updateState {
                        copy(
                            nickname =nickname,
                            selectedCharacterIndex = index
                        )
                    }
                }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val selectedCharacterId = resolveNicknameFor(sharePreferenceRepository.getCurrentSelectedCharacterIndex())
            updateState {
                copy(
                    nickname = selectedCharacterId,
                )
            }
        }
    }

    fun fetchRecommendKeyword(force: Boolean) {
        if (currentState.isLoading && !force) return

        keywordJob?.cancel()
        keywordJob = viewModelScope.launch {
            homeRepository.getKeyword()
                .onStart { updateState { copy(isLoading = true, error = null) } }
                .catch { e ->
                    updateState { copy(isLoading = false, error = e.message ?: "Unknown Error") }
                }
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            updateState { copy(isLoading = true) }
                        }

                        is Resource.Success -> {
                            updateState {
                                copy(
                                    isLoading = false,
                                    recommendKeyword = result.data,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            updateState {
                                copy(
                                    isLoading = false,
                                    error = result.message ?:context.getString(R.string.error_unknown)
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun openNicknameDialog() {
        updateState {
            copy(
                isEditNicknameDialogOpen = true,
                nicknameDialogState = this.nicknameDialogState.copy(nickname = this.nickname)
            )
        }
    }

    private fun onNicknameChange(newNickname: String) {
        val helperTextResId =
            if (newNickname.isEmpty()) R.string.nickname_edit_dialog_guide_msg
            else R.string.good_nickname_msg

        updateState {
            copy(
                nicknameDialogState = this.nicknameDialogState.copy(
                    nickname = newNickname,
                    isError = false,
                    helperText = UiText.FromRes(helperTextResId)
                )
            )
        }
    }

    private fun onSaveNickName() {
        viewModelScope.launch {
            val newNickname = currentState.nicknameDialogState.nickname
            sharePreferenceRepository.saveNickname(newNickname)
            updateState {
                copy(
                    nickname = newNickname,
                    isEditNicknameDialogOpen = false
                )
            }
            sendEffect(HomeContract.HomeEffect.ShowSnackBar(UiText.FromRes(R.string.saved_nickname_text)))
        }
    }

    private fun getDefaultNicknameForCharacter(index: Int): String =
        when(index) {
            Config.GILL_GILL_MON -> context.getString(R.string.gill_gill_mon)
            Config.HEE_HEE_MON -> context.getString(R.string.hee_hee_mon)
            Config.KICK_KICK_MON -> context.getString(R.string.kick_kick_mon)
            else -> {context.getString(R.string.gill_gill_mon)}
        }

    private suspend fun resolveNicknameFor(index: Int): String {
        var nickname = sharePreferenceRepository.getNickname()
        if (nickname.isEmpty()) {
            nickname = getDefaultNicknameForCharacter(index)
        }
        return nickname
    }
}
