package com.joke.mon.feature.result_joke.data.source.remote.dto

import com.google.gson.annotations.SerializedName
data class JokeResponse(
    @SerializedName("result")
    val jokeResult : String
)
