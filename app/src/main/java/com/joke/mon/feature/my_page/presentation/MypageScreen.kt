package com.joke.mon.feature.my_page.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.joke.mon.R
import com.joke.mon.feature.main.presentaion.BottomNavItem
import com.joke.mon.ui.component.MyPageCategory
import com.joke.mon.ui.navigation.AppRoute
import com.joke.mon.ui.theme.Grayscale150
import com.joke.mon.ui.theme.JokeMonTheme
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
fun MyPageScreen(
    bottomNavController: NavController,
    topLevelNavController: NavHostController,
    viewModel: MyPageViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MyPageContract.MyPageEffect.NavigateToLikedJokes -> {
                    bottomNavController.navigate(BottomNavItem.Like.route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                MyPageContract.MyPageEffect.NavigateToRecentJokes -> {
                    topLevelNavController.navigate(AppRoute.RECENT_JOKE)
                }

                MyPageContract.MyPageEffect.NavigateToNotificationSetting ->  {
                     topLevelNavController.navigate(AppRoute.NOTIFICATION_SETTING)
                }

                MyPageContract.MyPageEffect.NavigateSelectCharacterScreen ->  {
                    topLevelNavController.navigate(AppRoute.SELECT_CHARACTER)
                }

                MyPageContract.MyPageEffect.OpenContactEmail -> {
                    val email = context.getString(R.string.email_address)
                    val subject = context.getString(R.string.email_title)

                    val uri = Uri.parse("mailto:").buildUpon().apply {
                        appendQueryParameter("to", email)
                        appendQueryParameter("subject", subject)
                    }.build()

                    val intent = Intent(Intent.ACTION_SENDTO, uri)

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_not_install_email_app),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is MyPageContract.MyPageEffect.ShowSnackBar ->  {

                }
            }
        }
    }


    MyPageContent(uiState , viewModel::sendEvent)
}

@Composable
fun MyPageContent(
    uiState: MyPageContract.MyPageState,
    onEvent: (MyPageContract.MyPageEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = stringResource(R.string.my_page_title) ,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp )
        )

        Spacer(modifier = Modifier.height(32.dp))


        Text(
            text = stringResource(R.string.joke_text),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        )

        MyPageCategory(
            modifier = Modifier.padding(vertical = 24.dp),
            R.drawable.ic_small_heart,
            stringResource(R.string.liked_joke)
        ) {
            onEvent(MyPageContract.MyPageEvent.OnLikedJokeClicked)
        }

        MyPageCategory(
            modifier = Modifier.padding(bottom = 24.dp),
            R.drawable.ic_history,
            stringResource(R.string.recent_created_joke)
        ) {
            onEvent(MyPageContract.MyPageEvent.OnRecentJokeClicked)
        }

        HorizontalDivider(thickness = 1.dp,  color = Grayscale150)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.setting),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        )

        MyPageCategory(
            modifier = Modifier.padding(vertical = 24.dp),
            R.drawable.ic_character_select,
            stringResource(R.string.select_character)
        ) {
            onEvent(MyPageContract.MyPageEvent.OnSelectCharacterClicked)
        }

        MyPageCategory(
            modifier = Modifier.padding(bottom = 24.dp),
            R.drawable.ic_notification,
            stringResource(R.string.notification_setting)
        ) {
            onEvent(MyPageContract.MyPageEvent.OnNotificationSettingClicked)
        }

        MyPageCategory(
            modifier = Modifier.padding(bottom = 24.dp),
            R.drawable.ic_service,
            stringResource(R.string.contact_us)
        ) {
            onEvent(MyPageContract.MyPageEvent.OnContactUsClicked)
        }

        MyPageCategory(
            modifier = Modifier.padding(bottom = 24.dp),
            R.drawable.ic_card,
            stringResource(R.string.purchase_recovery)
        ) {

        }

    }
}

@Preview
@Composable
fun MyPageScreenPreview() {
    JokeMonTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            MyPageContent(
                uiState = MyPageContract.MyPageState(),
                onEvent = {}
            )
        }
    }
}