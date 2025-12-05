package com.joke.mon.feature.select_character.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.joke.mon.R
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.data.repository.SharePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCharacterViewModel @Inject constructor(
    @ApplicationContext private val context : Context,
    private val sharePreferenceRepository: SharePreferenceRepository,
) : BaseViewModel<SelectCharacterContract.SelectCharacterState ,
        SelectCharacterContract.SelectCharacterEvent , SelectCharacterContract.SelectCharacterEffect>()
{
    init {
        viewModelScope.launch {
           val  currentCharacterId = sharePreferenceRepository.getCurrentSelectedCharacterIndex()
            updateState { copy(selectedCharacterIndex = currentCharacterId)}
        }
    }

    override fun createInitialState(): SelectCharacterContract.SelectCharacterState = SelectCharacterContract.SelectCharacterState()

    override fun handleEvent(event: SelectCharacterContract.SelectCharacterEvent) {
        when(event) {
            SelectCharacterContract.SelectCharacterEvent.OnBackClicked -> {
                sendEffect(SelectCharacterContract.SelectCharacterEffect.NavigateBack)
            }
            is SelectCharacterContract.SelectCharacterEvent.OnCharacterSelected ->  {
                updateState { copy(selectedCharacterIndex = event.index) }
            }

            SelectCharacterContract.SelectCharacterEvent.OnSaveCharacterClicked -> {
                viewModelScope.launch {
                    sharePreferenceRepository.saveSelectedCharacterIndex(uiState.value.selectedCharacterIndex)
                sendEffect(SelectCharacterContract.SelectCharacterEffect.SaveCharacterNavigateBack(
                    context.getString(R.string.selected_character_snack_bar_msg)))
                }
            }
        }
    }

}