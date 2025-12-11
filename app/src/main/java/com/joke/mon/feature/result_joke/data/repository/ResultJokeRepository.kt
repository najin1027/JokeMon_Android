package com.joke.mon.feature.result_joke.data.repository

import com.joke.mon.core.util.Resource
import com.joke.mon.feature.result_joke.data.source.remote.dto.JokeResponse
import kotlinx.coroutines.flow.Flow

interface ResultJokeRepository
{
    fun fetchJoke(prompt : String) : Flow<Resource<JokeResponse>>
}