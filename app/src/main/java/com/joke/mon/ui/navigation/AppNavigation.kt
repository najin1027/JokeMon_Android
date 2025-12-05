package com.joke.mon.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joke.mon.feature.home.presentation.HomeScreen
import com.joke.mon.feature.like.presentation.LikeScreen
import com.joke.mon.feature.main.presentaion.BottomNavItem
import com.joke.mon.feature.main.presentaion.MainScreen
import com.joke.mon.feature.my_page.presentation.MyPageScreen
import com.joke.mon.feature.noti_setting.presentaion.NotificationSettingScreen
import com.joke.mon.feature.recent_joke.presentation.RecentJokeScreen
import com.joke.mon.feature.result_joke.presentation.CATEGORY_ID_KEY
import com.joke.mon.feature.result_joke.presentation.ENTRY_POINT_KEY
import com.joke.mon.feature.result_joke.presentation.JOKE_KEY
import com.joke.mon.feature.result_joke.presentation.KEYWORD_KEY
import com.joke.mon.feature.result_joke.presentation.ResultJokeEntryPoint
import com.joke.mon.feature.result_joke.presentation.ResultJokeScreen
import com.joke.mon.feature.search.presentation.SearchScreen
import com.joke.mon.feature.select_character.presentation.SelectCharacterScreen
import com.joke.mon.feature.splash.presentation.SplashRoute

@Composable
fun StartNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH
    ) {
        composable(AppRoute.SPLASH) {
            SplashRoute(
                onFinished = {
                    navController.navigate(AppRoute.MAIN) {
                        popUpTo(AppRoute.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.MAIN) {
            MainScreen(topLevelNavController = navController)
        }

        composable(
            route = "${AppRoute.RESULT_JOKE}?$JOKE_KEY={$JOKE_KEY}&$CATEGORY_ID_KEY={$CATEGORY_ID_KEY}" +
                    "&$KEYWORD_KEY={$KEYWORD_KEY}&$ENTRY_POINT_KEY={$ENTRY_POINT_KEY}",
            arguments = listOf(
                navArgument(JOKE_KEY) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(CATEGORY_ID_KEY) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(KEYWORD_KEY) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(ENTRY_POINT_KEY) {
                    type = NavType.StringType
                    defaultValue = ResultJokeEntryPoint.UNKNOWN.name
                }
            )
        ) {
            ResultJokeScreen(navController = navController)
        }


        composable(
            route = AppRoute.NOTIFICATION_SETTING
        ) {
            NotificationSettingScreen(navController = navController)
        }


        composable(
            route = AppRoute.RECENT_JOKE
        ) {
            RecentJokeScreen(navController = navController)
        }


        composable(
            route = AppRoute.SELECT_CHARACTER
        ) {
            SelectCharacterScreen(navController = navController)
        }
    }
}

@Composable
fun MainBottomBarNavigationGraph(
    bottomNavController: NavHostController,
    rootNavController: NavHostController,
    showSnackBar: (String) -> Unit
) {
    NavHost(navController = bottomNavController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController = rootNavController, showSnackBar = showSnackBar)
        }
        composable(BottomNavItem.Search.route) {
            SearchScreen(navController = rootNavController)
        }
        composable(BottomNavItem.Like.route) {
            LikeScreen(navController = rootNavController)
        }

        composable(BottomNavItem.MyPage.route) {
            MyPageScreen(bottomNavController = bottomNavController , topLevelNavController = rootNavController)
        }
    }
}

object ResultJokeRoute {
    const val base = AppRoute.RESULT_JOKE

    fun createRoute(
        jokeKey: String = "",
        categoryId: Int = -1,
        keyword: String? = null,
        entryPoint: ResultJokeEntryPoint = ResultJokeEntryPoint.UNKNOWN
    ): String {
        val params = buildList {
            add("$JOKE_KEY=$jokeKey")
            add("$CATEGORY_ID_KEY=$categoryId")
            keyword?.let { add("$KEYWORD_KEY=${Uri.encode(it)}") }
            add("$ENTRY_POINT_KEY=${entryPoint.name}")
        }.joinToString("&")

        return "$base?$params"
    }
}

object AppRoute {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val NOTIFICATION_SETTING = "notification_setting_screen"
    const val RECENT_JOKE = "recent_joke_screen"
    const val SELECT_CHARACTER = "select_character_screen"
    const val RESULT_JOKE = "result_joke_screen"
}