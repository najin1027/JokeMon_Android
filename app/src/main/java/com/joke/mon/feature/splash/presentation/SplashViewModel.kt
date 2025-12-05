package com.joke.mon.feature.splash.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joke.mon.feature.splash.data.repository.SplashRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
) : ViewModel()
{
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()


    private val _effect = MutableSharedFlow<SplashContract.SplashEffect>()
    val effect = _effect.asSharedFlow()


    init {
        viewModelScope.launch {
            delay(900)
            _isReady.value = true
        }
    }
}



@Composable
fun SplashRoute(
    onFinished: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isReady by viewModel.isReady.collectAsStateWithLifecycle()

    LaunchedEffect(isReady) {
        if (isReady) onFinished()
    }
    SplashScreen()
}