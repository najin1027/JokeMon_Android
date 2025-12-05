package com.joke.mon.feature.noti_setting.presentaion

import com.joke.mon.core.base.BaseContract

object NotificationSettingContract : BaseContract<NotificationSettingContract.NotificationSettingState ,
        NotificationSettingContract.NotificationSettingEvent , NotificationSettingContract.NotificationSettingEffect>
{
    data class NotificationSettingState
        (
            val isLoading : Boolean = true,
            val isPushEnable : Boolean = true,
    ): BaseContract.State



    sealed interface NotificationSettingEvent : BaseContract.Event
    {
        data class OnPushToggle(val isEnable : Boolean) : NotificationSettingEvent
        data object OnBackBtnClicked : NotificationSettingEvent
    }

    sealed interface NotificationSettingEffect : BaseContract.Effect
    {
        data object NavigateToBack : NotificationSettingEffect
    }
}