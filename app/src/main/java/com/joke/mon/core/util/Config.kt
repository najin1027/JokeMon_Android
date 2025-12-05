package com.joke.mon.core.util

object Config
{
   const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.joke.mon"
    const val GILL_GILL_MON = 0
    const val HEE_HEE_MON = 1
    const val KICK_KICK_MON = 2


    const val JOKE_CATEGORY_COUNT = 3
    const val JOKE_CATEGORY_1 = 1
    const val JOKE_CATEGORY_2 = 2
    const val JOKE_CATEGORY_3 = 3

    const val VOICE_ID_MALE = 0
    const val VOICE_ID_FEMALE = 1
    const val VOICE_ID_QUICK = 2
    const val VOICE_ID_SLOW = 3
    const val VOICE_ID_CUTE = 4
    const val VOICE_ID_MYSTERY = 5


    const val BASE_URL = "https://us-central1-jokemon-20f29.cloudfunctions.net/"

    const val KEY_CURRENT_SELECTED_CHARACTER_INDEX = "KEY_CURRENT_SELECTED_CHARACTER_INDEX"
    const val KEY_NICK_NAME = "KEY_NICK_NAME"

    const val KEY_LAST_REVIEW_DIALOG_TIME = "KEY_LAST_REVIEW_DIALOG_TIME"
    const val REVIEW_DIALOG_INTERVAL_MILLIS = 4L * 24 * 60 * 60 * 1000
    const val KEY_NOTIFICATION_IS_ENABLED = "KEY_NOTIFICATION_IS_ENABLED"

    const val JOKE_CATEGORY_1_NAME = "요즘유행"
    const val JOKE_CATEGORY_2_NAME = "아재감성"
    const val JOKE_CATEGORY_3_NAME = "어이없잼"

    enum class SortOption { LATEST, OLDEST }
}