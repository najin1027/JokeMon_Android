package com.joke.mon.feature.select_character.presentation

import com.joke.mon.core.base.BaseContract
import com.joke.mon.core.data.model.CharacterData
import com.joke.mon.core.data.model.Characters

object SelectCharacterContract : BaseContract<SelectCharacterContract.SelectCharacterState ,
        SelectCharacterContract.SelectCharacterEvent , SelectCharacterContract.SelectCharacterEffect>
{
    data class SelectCharacterState(
        val isLoading : Boolean = false,
        val selectedCharacterIndex : Int = 0
    ) :  BaseContract.State
    {

    val selectedCharacter: CharacterData
        get() = Characters.getOrElse(selectedCharacterIndex) { Characters.first() }
    }

    sealed interface SelectCharacterEvent : BaseContract.Event {
        data object OnBackClicked : SelectCharacterEvent
        data class OnCharacterSelected(val index: Int) : SelectCharacterEvent
        data object OnSaveCharacterClicked : SelectCharacterEvent
    }

    sealed interface SelectCharacterEffect : BaseContract.Effect {
        data object NavigateBack : SelectCharacterEffect
        data class SaveCharacterNavigateBack(val message: String) : SelectCharacterEffect
    }
}