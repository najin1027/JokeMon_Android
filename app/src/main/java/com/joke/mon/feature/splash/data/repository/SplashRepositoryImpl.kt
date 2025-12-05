package com.joke.mon.feature.splash.data.repository

import com.joke.mon.feature.splash.data.source.remote.api.SplashApiService
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(
 private val splashApi: SplashApiService,
) : SplashRepository {

}