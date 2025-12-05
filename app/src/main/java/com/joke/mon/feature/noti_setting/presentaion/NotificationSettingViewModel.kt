package com.joke.mon.feature.noti_setting.presentaion

import androidx.lifecycle.viewModelScope
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.data.repository.SharePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val sharePreferenceRepository: SharePreferenceRepository
) : BaseViewModel<NotificationSettingContract.NotificationSettingState ,
        NotificationSettingContract.NotificationSettingEvent , NotificationSettingContract.NotificationSettingEffect>()
{
    init
    {
        getIsNotificationEnabled()
    }

    override fun createInitialState(): NotificationSettingContract.NotificationSettingState {
        return NotificationSettingContract.NotificationSettingState()
    }

    override fun handleEvent(event: NotificationSettingContract.NotificationSettingEvent) {
        when(event) {
           is NotificationSettingContract.NotificationSettingEvent.OnPushToggle ->  {
               viewModelScope.launch {
                   sharePreferenceRepository.saveNotificationIsEnabled(event.isEnable)
                   updateState { copy(isPushEnable = event.isEnable) }
               }
            }

            NotificationSettingContract.NotificationSettingEvent.OnBackBtnClicked ->  {
                sendEffect(NotificationSettingContract.NotificationSettingEffect.NavigateToBack)
            }
        }
    }


    private fun getIsNotificationEnabled()  {
        viewModelScope.launch {
            val isPushEnabled = sharePreferenceRepository.getNotificationIsEnabled()
            updateState { copy(
                isPushEnable = isPushEnabled
            ) }
        }
    }

}