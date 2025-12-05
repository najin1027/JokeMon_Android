package com.joke.mon.feature.home.data.repository

import com.joke.mon.core.util.Resource
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse
import kotlinx.coroutines.flow.Flow

interface HomeRepository
{
    suspend fun getKeyword() : Flow<Resource<KeywordResponse>>
}