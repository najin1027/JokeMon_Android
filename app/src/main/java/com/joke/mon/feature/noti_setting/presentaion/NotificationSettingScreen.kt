package com.joke.mon.feature.noti_setting.presentaion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joke.mon.R
import com.joke.mon.ui.component.CommonAppBar
import com.joke.mon.ui.theme.Grayscale100
import com.joke.mon.ui.theme.Grayscale600
import com.joke.mon.ui.theme.JokeMonTheme

@Composable
fun NotificationSettingScreen (navController: NavController, viewModel: NotificationSettingViewModel = hiltViewModel())
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                NotificationSettingContract.NotificationSettingEffect.NavigateToBack ->  {
                        navController.popBackStack()
                }
            }

        }
    }

    NotificationSettingContent(uiState  , viewModel::sendEvent)
}

@Composable
fun NotificationSettingContent(
    uiState : NotificationSettingContract.NotificationSettingState,
    onEvent : (NotificationSettingContract.NotificationSettingEvent) -> Unit
)
{
    Scaffold {paddingValues ->
        Column (modifier = Modifier
            .fillMaxSize().padding(paddingValues)
            .padding(horizontal = 16.dp)) {
            CommonAppBar(
                title = stringResource(R.string.notification_setting),
                leftIconPainter = painterResource(R.drawable.ic_back_arrow),
                onLeftIconClick = {
                    onEvent(NotificationSettingContract.NotificationSettingEvent.OnBackBtnClicked)
                }
            )

            PushNotificationSettingItem(
                uiState.isPushEnable,
                {isEnable ->
                    onEvent(NotificationSettingContract.NotificationSettingEvent.OnPushToggle(isEnable))
                }
            )
        }
    }


}

@Composable
fun PushNotificationSettingItem(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.push_noti),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )

            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }


        Text(
            text = stringResource(R.string.notification_guide_msg),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = Grayscale600
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(thickness = 1.dp, color = Grayscale100)
    }
}

@Preview
@Composable
fun NotificationSettingContentPreview()
{
    val uiState = NotificationSettingContract.NotificationSettingState()

    JokeMonTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            NotificationSettingContent(
                uiState
            ) { }
        }
    }
}