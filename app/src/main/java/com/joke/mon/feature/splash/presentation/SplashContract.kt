package com.joke.mon.feature.splash.presentation

class SplashContract
{

//    data class SplashState(
//        val isLoading: Boolean = false,
//        val homeData: YourDataModel? = null,
//        val error: String? = null
//    )

    sealed interface SplashEvent {
        object OnScreenLoad : SplashEvent
        object OnRefreshClicked : SplashEvent
        data class OnItemClicked(val itemId: String) : SplashEvent
    }


    sealed interface SplashEffect {
        data class NavigateToDetail(val itemId: String) : SplashEffect
        data class ShowSnackbar(val message: String) : SplashEffect
    }
}