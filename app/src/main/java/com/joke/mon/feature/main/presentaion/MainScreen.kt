package com.joke.mon.feature.main.presentaion

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.joke.mon.ui.navigation.MainBottomBarNavigationGraph
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.joke.mon.R
import com.joke.mon.core.util.Utils.openPlayStoreReview
import com.joke.mon.ui.component.CommonSnackBar
import com.joke.mon.ui.component.ExitDialog
import com.joke.mon.ui.component.ReviewRequestDialog
import com.joke.mon.ui.theme.GrayscaleBlack
import com.joke.mon.ui.theme.GrayscaleWhite
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun MainScreen(topLevelNavController: NavHostController , viewModel: MainViewModel = hiltViewModel())
{
    val bottomBarNavController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = context as? Activity

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        } else {

        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                MainContract.MainEffect.ExitApp -> {
                    activity?.finish()
                }
                MainContract.MainEffect.OpenStoreReview -> {
                    openPlayStoreReview(context)
                }
            }
        }
    }

    LaunchedEffect(notificationPermission) {
        if (notificationPermission == null) return@LaunchedEffect

        val granted = ContextCompat.checkSelfPermission(
            context,
            notificationPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            launcher.launch(notificationPermission)
        }
    }

    BackHandler {
        viewModel.sendEvent(MainContract.MainEvent.OnBackPressed)
    }

    Scaffold(
        snackbarHost =  { SnackbarHost(hostState = snackBarHostState) { snackBarData ->
            CommonSnackBar(snackBarData = snackBarData)
        } },
        bottomBar = { BottomNavigationBar(navController = bottomBarNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainBottomBarNavigationGraph(
                bottomNavController = bottomBarNavController,
                rootNavController = topLevelNavController,
                showSnackBar = { message ->
                    scope.launch {
                        snackBarHostState.showSnackbar(message)
                    }
                }
            )
        }

        if (uiState.showReviewDialog) {
            Dialog(onDismissRequest = {
                viewModel.sendEvent(MainContract.MainEvent.OnReviewCancelClicked)
            }) {
                ReviewRequestDialog(
                    onWriteReview = {
                        viewModel.sendEvent(MainContract.MainEvent.OnReviewWriteClicked)
                    },
                    onCancel = {
                        viewModel.sendEvent(MainContract.MainEvent.OnReviewCancelClicked)
                    },
                    onExit = {
                        viewModel.sendEvent(MainContract.MainEvent.OnReviewExitClicked)
                    }
                )
            }
        }


        if (uiState.showExitDialog) {
            Dialog(onDismissRequest = {
                viewModel.sendEvent(MainContract.MainEvent.OnExitCancelClicked)
            }) {
                ExitDialog(
                    onExit = {
                        viewModel.sendEvent(MainContract.MainEvent.OnExitConfirmClicked)
                    },
                    onCancel = {
                        viewModel.sendEvent(MainContract.MainEvent.OnExitCancelClicked)
                    }
                )
            }
        }

    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Like,
        BottomNavItem.MyPage
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(modifier = Modifier.navigationBarsPadding()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp , topEnd = 24.dp)),
            color = GrayscaleBlack,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
                    .height(90.dp),

                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Top
            ) {
                items.forEach { screen ->
                    BottomBarItem(
                        item = screen,
                        currentDestination = currentDestination,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomBarItem(
    item: BottomNavItem,
    currentDestination: NavDestination?,
    onClick: () -> Unit
) {
    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else Color.Transparent), // 선택 상태에 따라 배경색 변경
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (isSelected) painterResource(item.selectionIcon)  else painterResource(item.unSelectionIcon),
                contentDescription = item.title,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else GrayscaleWhite // 색상 변경
            )
        }
    }
}


sealed class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val unSelectionIcon: Int,
    @DrawableRes val selectionIcon: Int
) {
    object Home : BottomNavItem("home", "홈", unSelectionIcon = R.drawable.ic_bottom_home_default , selectionIcon = R.drawable.ic_bottom_home_selection)
    object Search : BottomNavItem("search", "검색", unSelectionIcon = R.drawable.ic_bottom_search_default , selectionIcon = R.drawable.ic_bottom_search_selection)
    object Like : BottomNavItem("like", "저장", unSelectionIcon = R.drawable.ic_bottom_like_default , selectionIcon = R.drawable.ic_bottom_like_selection)
    object MyPage : BottomNavItem("my_page", "마이페이지", unSelectionIcon = R.drawable.ic_bottom_my_page_default , selectionIcon = R.drawable.ic_bottom_my_page_selection)
}

