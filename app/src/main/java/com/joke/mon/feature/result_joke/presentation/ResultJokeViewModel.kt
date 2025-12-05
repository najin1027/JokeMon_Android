package com.joke.mon.feature.result_joke.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.joke.mon.R
import com.joke.mon.core.analytics.AnalyticsHelper
import com.joke.mon.core.base.BaseViewModel
import com.joke.mon.core.data.model.JokeCategory
import com.joke.mon.core.data.repository.SharePreferenceRepository
import com.joke.mon.core.data.repository.VoiceSettingsRepository
import com.joke.mon.core.data.soruce.local.entity.BaseJoke
import com.joke.mon.core.util.Config.GILL_GILL_MON
import com.joke.mon.core.util.Config.HEE_HEE_MON
import com.joke.mon.core.util.Config.JOKE_CATEGORY_1_NAME
import com.joke.mon.core.util.Config.JOKE_CATEGORY_2_NAME
import com.joke.mon.core.util.Config.JOKE_CATEGORY_3_NAME
import com.joke.mon.core.util.Config.KICK_KICK_MON
import com.joke.mon.core.util.Config.PLAY_STORE_URL
import com.joke.mon.core.util.ElevenLabsTTSManager
import com.joke.mon.core.util.Resource
import com.joke.mon.core.util.Utils.getFormattedNow
import com.joke.mon.core.util.Utils.getJokeCategoryEmoji
import com.joke.mon.core.util.Utils.getJokeCategoryName
import com.joke.mon.feature.like.data.repository.LikeRepository
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import com.joke.mon.feature.recent_joke.data.repository.RecentJokeRepository
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import com.joke.mon.feature.result_joke.data.repository.ResultJokeRepository
import com.joke.mon.feature.result_joke.presentation.ResultJokeContract.ResultJokeEffect.*
import com.joke.mon.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


const val JOKE_KEY = "jokeKey"
const val CATEGORY_ID_KEY = "categoryId"
const val KEYWORD_KEY = "keyword"
const val ENTRY_POINT_KEY = "entryPoint"

@HiltViewModel
class ResultJokeViewModel @Inject constructor(
    @ApplicationContext private val context : Context,
    private val savedStateHandle: SavedStateHandle,
    private val resultJokeRepository: ResultJokeRepository,
    private val likeJokeRepository: LikeRepository,
    private val recentJokeRepository : RecentJokeRepository,
    private val voiceSettingsRepository: VoiceSettingsRepository,
    private val sharePreferenceRepository: SharePreferenceRepository,
    private val ttsManager: ElevenLabsTTSManager,
    private val analytics: AnalyticsHelper,
    ) :
    BaseViewModel<ResultJokeContract.ResultJokeState , ResultJokeContract.ResultJokeEvent , ResultJokeContract.ResultJokeEffect>()
{

    val jokeKey: String
    val categoryId: Int
    val keyword: String?
    val entryPoint: ResultJokeEntryPoint

    init {
       jokeKey  = savedStateHandle[JOKE_KEY] ?:""
       categoryId = savedStateHandle[CATEGORY_ID_KEY] ?: -1
       keyword =  savedStateHandle[KEYWORD_KEY]
       val entryPointName: String? = savedStateHandle[ENTRY_POINT_KEY]
       entryPoint = ResultJokeEntryPoint.entries.firstOrNull { it.name == entryPointName } ?: ResultJokeEntryPoint.UNKNOWN

        analytics.logScreenView(AppRoute.RESULT_JOKE)

        loadSelectedCharacterIndex()

        updateState {
            copy(
                jokeCategoryId = categoryId,
                category = JokeCategory(
                    id = categoryId,
                    text = getJokeCategoryName(categoryId),
                    emoji = getJokeCategoryEmoji(categoryId)
                ),
                errorMessage = null
            )
        }

        when(entryPoint) {
            ResultJokeEntryPoint.FROM_SEARCH ->  {
                if (categoryId != -1 && !keyword.isNullOrEmpty()) {
                    generateJokeFromAI(categoryId, keyword)
                } else {
                    updateState { copy(isLoading = false, errorMessage = context.getString(R.string.error_wrong_approach)) }
                }
            }
            ResultJokeEntryPoint.FROM_LIKE_LIST -> {
                if (jokeKey.isNotEmpty()) {
                    loadJokeFromLikeDatabase(jokeKey)
                } else {
                    updateState { copy(isLoading = false, errorMessage = context.getString(R.string.error_wrong_approach)) }
                }
            }
            ResultJokeEntryPoint.FROM_RECENT_LIST ->  {
                if (jokeKey.isNotEmpty()) {
                    loadJokeFromRecentDatabase(jokeKey)
                } else {
                    updateState { copy(isLoading = false, errorMessage = context.getString(R.string.error_wrong_approach)) }
                }
            }
            ResultJokeEntryPoint.UNKNOWN -> {
                updateState {
                    copy(
                        isLoading = false,
                        jokeContent = context.getString(R.string.error_wrong_approach),
                        errorMessage = context.getString(R.string.error_wrong_approach)
                    )
                }
            }
        }


        viewModelScope.launch {
            voiceSettingsRepository.voiceConfigFlow.collect { savedConfig ->
                updateState {
                    copy(voiceConfig = savedConfig)
                }
            }
        }

        viewModelScope.launch {
            ttsManager.isPlaying.collect { isPlaying ->
                updateState { copy(isJokePlaying = isPlaying) }
            }
        }
    }

    fun generateJokeKey(): String {
        return UUID.randomUUID().toString()
    }

    override fun createInitialState(): ResultJokeContract.ResultJokeState {
        return ResultJokeContract.ResultJokeState()
    }

    override fun handleEvent(event: ResultJokeContract.ResultJokeEvent) {
        when (event) {
            ResultJokeContract.ResultJokeEvent.OnBackClicked ->
                sendEffect(NavigateBack)

            ResultJokeContract.ResultJokeEvent.OnHomeClicked ->
                sendEffect(NavigateToHome)

            is ResultJokeContract.ResultJokeEvent.OnCopyClicked -> {
                sendEffect(CopyToClipboard(event.content))
            }

            ResultJokeContract.ResultJokeEvent.OnLikeToggled -> {
                val currentState = uiState.value
                val newLikedState = !uiState.value.isLiked
                updateState { copy(isLiked = newLikedState) }
                val message = if (newLikedState) context.getString(R.string.save_joke) else context.getString(R.string.un_save_joke)
                sendEffect(ShowSnackBar(message))

                viewModelScope.launch {
                    if (newLikedState) {
                        val likeJoke = LikeJoke(
                            key = currentState.jokeKey,
                            content = currentState.jokeContent,
                            categoryId = currentState.jokeCategoryId,
                            createdAt = currentState.createdAt,
                            keyword = currentState.keyword,
                            isSaved = true
                        )
                        likeJokeRepository.addJoke(likeJoke)

                    } else {
                        likeJokeRepository.deleteJokeByKey(currentState.jokeKey)
                    }
                }
            }

            ResultJokeContract.ResultJokeEvent.OnRegenerateClicked -> {
                generateJokeFromAI(categoryId = uiState.value.jokeCategoryId , keyword = uiState.value.keyword)
            }

            ResultJokeContract.ResultJokeEvent.OnOpenVoiceSettings -> {
                updateState { copy(isVoiceSheetVisible = true) }
            }

            ResultJokeContract.ResultJokeEvent.OnCloseVoiceSettings -> {
                updateState { copy(isVoiceSheetVisible = false) }
            }

            is ResultJokeContract.ResultJokeEvent.OnApplyVoiceSettings -> {
                viewModelScope.launch {
                    voiceSettingsRepository.saveVoiceConfig(event.config)


                    updateState {
                        copy(
                            voiceConfig = event.config,
                            isVoiceSheetVisible = false
                        )
                    }
                }
            }

            ResultJokeContract.ResultJokeEvent.OnPlayJokeClicked -> {
                viewModelScope.launch {
                    val currentState = uiState.value
                    val voiceConfig = currentState.voiceConfig

                    if (currentState.isJokePlaying) {
                        ttsManager.stop()
                    }
                    else {
                        updateState { copy(isVoiceLoading = true) }

                        try {
                            ttsManager.speak(
                                currentState.jokeContent,
                                voiceConfig.voiceId,
                                config = voiceConfig
                            )
                        }
                        catch (e : Exception) {
                            sendEffect(ShowSnackBar(context.getString(R.string.error_tts_play)))
                        }
                        finally {
                            updateState { copy(isVoiceLoading = false) }
                        }
                    }
                }
            }

            is ResultJokeContract.ResultJokeEvent.OnPreviewVoice ->  {
                viewModelScope.launch {
                    updateState { copy(isVoiceLoading = true) }

                    try {
                        val previewText = context.getString(R.string.tts_sample_msg)

                        ttsManager.speak(
                            text = previewText,
                            event.config.voiceId,
                            config = event.config
                        )
                    }
                    catch (e : Exception) {

                    }
                    finally {
                        updateState { copy(isVoiceLoading = false) }
                    }
                }
            }
            ResultJokeContract.ResultJokeEvent.OnStopVoice -> {
                ttsManager.stop()
            }

            ResultJokeContract.ResultJokeEvent.OnShareClicked ->  {
                val currentState = uiState.value

                val shareText = buildString {
                    append(context.getString(R.string.share_joke))
                    append(currentState.jokeContent)
                    append(context.getString(R.string.move_to_app))
                    append(PLAY_STORE_URL)
                }

                sendEffect(ShareJoke(shareText))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.release()
    }

    private fun loadSelectedCharacterIndex()  {
        viewModelScope.launch {
            val characterIndex = sharePreferenceRepository.getCurrentSelectedCharacterIndex()

            updateState { copy(selectedCharacterIndex = characterIndex) }
        }
    }

    private fun loadJokeByKey(
        jokeKey: String,
        fetchJoke: suspend (String) -> BaseJoke?,
        resolveIsLiked: suspend (BaseJoke) -> Boolean
    ) {
        viewModelScope.launch {
            val joke = fetchJoke(jokeKey)

            if (joke == null) {
                updateState {
                    copy(
                        isLoading = false,
                        jokeContent = context.getString(R.string.error_wrong_approach)
                    )
                }
                return@launch
            }

            val isLiked = resolveIsLiked(joke)

            val category = JokeCategory(
                id = joke.categoryId,
                text = getJokeCategoryName(joke.categoryId),
                emoji = getJokeCategoryEmoji(joke.categoryId)
            )

            updateState {
                copy(
                    isLoading = false,
                    createdAt = joke.createdAt,
                    jokeKey = joke.key,
                    keyword = joke.keyword,
                    jokeContent = joke.content,
                    jokeCategoryId = joke.categoryId,
                    isLiked = isLiked,
                    category = category
                )
            }
        }
    }

    private fun loadJokeFromLikeDatabase(jokeKey: String) {
        loadJokeByKey(
            jokeKey = jokeKey,
            fetchJoke = { key ->
                likeJokeRepository.getJokeByKey(key)
            },
            resolveIsLiked = { joke ->
                joke.isSaved
            }
        )
    }

    private fun loadJokeFromRecentDatabase(jokeKey : String) {
        loadJokeByKey(
            jokeKey = jokeKey,
            fetchJoke = { key ->
                recentJokeRepository.getRecentJokeByKey(key)
            },
            resolveIsLiked = { joke ->
                likeJokeRepository
                    .isJokeLiked(joke.key)
                    .first()
            }
        )
    }

    private fun generateJokeFromAI(categoryId: Int, keyword: String) {
        viewModelScope.launch {
             updateState { copy(isLoading = true) }

            try {
                val characterIndex = sharePreferenceRepository.getCurrentSelectedCharacterIndex()

                val categoryLabel = getJokeCategoryName(categoryId)
                val category = when (categoryLabel) {
                    JOKE_CATEGORY_1_NAME -> context.getString(R.string.joke_category_1_prompt_title)
                    JOKE_CATEGORY_2_NAME -> context.getString(R.string.joke_category_2_prompt_title)
                    JOKE_CATEGORY_3_NAME -> context.getString(R.string.joke_category_3_prompt_title)
                    else -> categoryLabel
                }

                val style = when (characterIndex) {
                    GILL_GILL_MON -> context.getString(R.string.joke_category_1_system_prompt)
                    HEE_HEE_MON ->context.getString(R.string.joke_category_2_system_prompt)
                    KICK_KICK_MON -> context.getString(R.string.joke_category_3_system_prompt)
                    else -> context.getString(R.string.joke_category_1_system_prompt)
                }


                val prompt = "$keyword 관련된 $category 해줘 , 말투는 $style 로 해줘"

                resultJokeRepository.fetchJoke(prompt).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            updateState { copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            val jokeContent = resource.data?.jokeResult ?: context.getString(R.string.error_failed_create_joke)

                             val key = generateJokeKey()
                             val createdAt = getFormattedNow()

                             val recentJoke = RecentJoke(
                                 key = key ,
                                 content = jokeContent,
                                 categoryId = categoryId,
                                 createdAt = createdAt,
                                 keyword = keyword,
                                 isSaved = false
                             )

                             recentJokeRepository.addRecentJokeWithLimit(recentJoke)


                            updateState {
                                copy(
                                    isLoading = false,
                                    jokeKey=key,
                                    keyword = keyword,
                                    jokeContent = jokeContent,
                                    jokeCategoryId = categoryId,
                                    isLiked = false,
                                    createdAt = createdAt,
                                    category = JokeCategory(
                                        id = categoryId,
                                        text = getJokeCategoryName(categoryId),
                                        emoji = getJokeCategoryEmoji(categoryId)
                                    ),
                                    errorMessage = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            val errorString = resource.message ?: "Unknown Error"

                            val errorMessage = if (errorString.contains("503") || errorString.contains("overloaded")) {
                                context.getString(R.string.error_overloaded)
                            } else if (errorString.contains("timeout", ignoreCase = true)) {
                                context.getString(R.string.error_joke_timeout)
                            } else {
                               context.getString(R.string.error_default)
                            }

                            updateState {
                                copy(
                                    isLoading = false,
                                    jokeContent = errorMessage,
                                    errorMessage = errorMessage,
                                    jokeCategoryId = categoryId,
                                    isLiked = false,
                                    category = JokeCategory(
                                        id = categoryId,
                                        text = getJokeCategoryName(categoryId),
                                        emoji = getJokeCategoryEmoji(categoryId)
                                    ),
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        jokeContent = "알 수 없는 오류가 발생했습니다",
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}

enum class ResultJokeEntryPoint {
    FROM_SEARCH,
    FROM_LIKE_LIST,
    FROM_RECENT_LIST,
    UNKNOWN
}