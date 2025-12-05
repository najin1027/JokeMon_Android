package com.joke.mon.core.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class FromRes(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText()

    data class Dynamic(val value: String) : UiText()

    fun resolve(context: Context): String = when (this) {
        is FromRes ->
            if (args.isEmpty()) context.getString(resId)
            else context.getString(resId, *args.toTypedArray())
        is Dynamic -> value
    }
}