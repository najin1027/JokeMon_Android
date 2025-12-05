package com.joke.mon.core.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.google.android.play.core.review.ReviewManagerFactory
import com.joke.mon.R
import com.joke.mon.ui.theme.Primary050
import com.joke.mon.ui.theme.SubBlue300
import com.joke.mon.ui.theme.SubGreen100
import com.joke.mon.ui.theme.SubGreen300
import com.joke.mon.ui.theme.SubRed100
import com.joke.mon.ui.theme.SubRed300
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.net.toUri

object Utils {

    fun getFormattedNow(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return now.format(formatter)
    }

    fun formatDateTime(dateTimeString: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(dateTimeString, inputFormat)

        val today = LocalDateTime.now()

        return if (dateTime.toLocalDate().isEqual(today.toLocalDate())) {
            val timeFormat = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)
            dateTime.format(timeFormat)
                .replace("AM", "오전")
                .replace("PM", "오후")
        } else {
            val pastFormat = DateTimeFormatter.ofPattern("yy.MM.dd.")
            dateTime.format(pastFormat)
        }
    }

    @Composable
    fun getJokeBackgroundColor(categoryId: Int): Color {
        return when (categoryId) {
            1 -> Primary050
            2 -> SubRed100
            3 -> SubGreen100
            else -> Primary050
        }
    }

    @Composable
    fun getJokeChipBackgroundColor(categoryId: Int): Color {
        return when (categoryId) {
            1 -> SubBlue300
            2 -> SubRed300
            3 -> SubGreen300
            else -> SubBlue300
        }
    }

    @Composable
    fun getImagePathByCategory(categoryId : Int, selectedCharacterId : Int) : Painter {
        return when(selectedCharacterId) {
            Config.GILL_GILL_MON -> {
                when (categoryId) {
                    Config.JOKE_CATEGORY_1 -> {
                        painterResource(R.drawable.trend_ggill_ggill_mon)
                    }
                    Config.JOKE_CATEGORY_2 -> {
                        painterResource(R.drawable.dad_ggil_ggil_mon)
                    }
                    else -> {
                        painterResource(R.drawable.simple_ggil_ggil_mon)
                    }
                }
            }

            Config.HEE_HEE_MON -> {
                when(categoryId) {
                    Config.JOKE_CATEGORY_1 -> {
                        painterResource(R.drawable.trend_hee_hee_mon)
                    }

                    Config.JOKE_CATEGORY_2 -> {
                        painterResource(R.drawable.dad_hee_hee_mon)
                    }

                    else -> {
                        painterResource(R.drawable.simple_hee_hee_mon)
                    }
                }
            }

            else -> {
                when(categoryId) {
                    Config.JOKE_CATEGORY_1 -> {
                        painterResource(R.drawable.trend_kick_kick_mon)
                    }

                    Config.JOKE_CATEGORY_2 -> {
                        painterResource(R.drawable.dad_kick_kick_mon)
                    }

                    else -> {
                        painterResource(R.drawable.simple_kick_kick_mon)
                    }
                }
            }
        }
    }

    fun getJokeCategoryName(categoryId: Int) : String {

        return when(categoryId) {
            Config.JOKE_CATEGORY_1 -> "요즘유행"

            Config.JOKE_CATEGORY_2 -> "아재감성"

            Config.JOKE_CATEGORY_3 -> "어이없잼"
            else -> "어이없잼"
        }
    }

    fun getJokeCategoryEmoji(categoryId: Int) : String  {
        return when(categoryId) {
            Config.JOKE_CATEGORY_1 -> "🙊"

            Config.JOKE_CATEGORY_2 -> "🧔🏻‍♂️"

            Config.JOKE_CATEGORY_3 -> "😎"
            else -> "😎"
        }
    }

    suspend fun openInAppReview(activity: Activity) {
        try {
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow().await()
            manager.launchReviewFlow(activity, request).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openPlayStoreReview(context: Context) {
        val packageName = context.packageName

        val uriMarket = "market://details?id=$packageName".toUri()
        val uriWeb = "https://play.google.com/store/apps/details?id=$packageName".toUri()

        try {
            val intent = Intent(Intent.ACTION_VIEW, uriMarket).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, uriWeb).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun SharedPreferences.intFlow(key: String, default: Int = 0): Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, changedKey ->
            if (changedKey == key) {
                trySend(sp.getInt(key, default))
            }
        }

        trySend(getInt(key, default))

        registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

}