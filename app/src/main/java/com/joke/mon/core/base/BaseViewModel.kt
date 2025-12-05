package com.joke.mon.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<
        S : BaseContract.State,
        E : BaseContract.Event,
        F : BaseContract.Effect> : ViewModel() {


    protected abstract fun createInitialState(): S

    private val _uiState: MutableStateFlow<S> =  MutableStateFlow(createInitialState())
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<E> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _effect = MutableSharedFlow<F>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    protected val currentState: S get() = _uiState.value

    init {
        subscribeEvents()
    }

    fun sendEvent(event: E) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    protected abstract fun handleEvent(event: E)

    protected fun updateState(reduce: S.() -> S) {
        _uiState.value = currentState.reduce()
    }


    protected fun sendEffect(effect: F) {
        viewModelScope.launch {
            _effect.tryEmit(effect)
        }
    }
}