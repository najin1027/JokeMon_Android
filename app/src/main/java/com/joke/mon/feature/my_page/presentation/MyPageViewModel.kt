package com.joke.mon.feature.my_page.presentation

import com.joke.mon.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MyPageViewModel @Inject constructor() : BaseViewModel<MyPageContract.MyPageState , MyPageContract.MyPageEvent , MyPageContract.MyPageEffect>()
{
    override fun createInitialState(): MyPageContract.MyPageState {
        return MyPageContract.MyPageState()
    }

    override fun handleEvent(event: MyPageContract.MyPageEvent) {
        when(event) {
            MyPageContract.MyPageEvent.OnLikedJokeClicked -> {
                sendEffect(MyPageContract.MyPageEffect.NavigateToLikedJokes)
            }
            MyPageContract.MyPageEvent.OnRecentJokeClicked ->  {
                sendEffect(MyPageContract.MyPageEffect.NavigateToRecentJokes)
            }

            MyPageContract.MyPageEvent.OnNotificationSettingClicked -> {
                sendEffect(MyPageContract.MyPageEffect.NavigateToNotificationSetting)
            }

            MyPageContract.MyPageEvent.OnSelectCharacterClicked -> {
                sendEffect(MyPageContract.MyPageEffect.NavigateSelectCharacterScreen)
            }

            MyPageContract.MyPageEvent.OnContactUsClicked ->  {
                sendEffect(MyPageContract.MyPageEffect.OpenContactEmail)
            }
        }
    }

}
