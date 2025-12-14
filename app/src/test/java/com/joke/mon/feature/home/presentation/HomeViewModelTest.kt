package com.joke.mon.feature.home.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.joke.mon.R
import com.joke.mon.core.data.repository.SharePreferenceRepository
import com.joke.mon.core.util.Config
import com.joke.mon.core.util.Resource
import com.joke.mon.core.util.UiText
import com.joke.mon.feature.home.data.repository.HomeRepository
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

/**
 * HomeViewModel의 단위 테스트
 *
 * 테스트 항목:
 * - 초기 상태 확인
 * - 키워드 추천 로딩
 * - 닉네임 변경
 * - 태그 클릭 시 네비게이션
 * - 캐릭터 선택 시 닉네임 업데이트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var homeRepository: HomeRepository
    private lateinit var sharePreferenceRepository: SharePreferenceRepository
    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        homeRepository = mock()
        sharePreferenceRepository = mock()
        context = mock()

        // Mock context strings
        whenever(context.getString(R.string.home_do_joke_btn_text)).thenReturn("농담하기")
        whenever(context.getString(R.string.error_unknown)).thenReturn("알 수 없는 오류")
        whenever(context.getString(R.string.error_internet_connect)).thenReturn("인터넷 연결 오류")
        whenever(context.getString(R.string.gill_gill_mon)).thenReturn("길길몬")
        whenever(context.getString(R.string.hee_hee_mon)).thenReturn("히히몬")
        whenever(context.getString(R.string.kick_kick_mon)).thenReturn("킥킥몬")
        whenever(context.getString(R.string.saved_nickname_text)).thenReturn("닉네임이 저장되었습니다")

        // Mock repository default behavior
        whenever(sharePreferenceRepository.observeCurrentSelectedCharacterIndex())
            .thenReturn(flowOf(Config.GILL_GILL_MON))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머", "웃긴이야기"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        // When
        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isEditNicknameDialogOpen)
        assertEquals("", state.nicknameDialogState.nickname)
        assertNull(state.error)
    }

    @Test
    fun `fetchRecommendKeyword should update state with success`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머", "웃긴이야기", "개그"))
        whenever(homeRepository.getKeyword()).thenReturn(
            flowOf(
                Resource.Loading(),
                Resource.Success(testKeyword)
            )
        )
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("테스트닉네임")

        // When
        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(testKeyword, state.recommendKeyword)
        assertNull(state.error)
    }

    @Test
    fun `fetchRecommendKeyword should update state with error`() = runTest {
        // Given
        val errorMessage = "네트워크 오류"
        whenever(homeRepository.getKeyword()).thenReturn(
            flowOf(Resource.Error(message = errorMessage))
        )
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        // When
        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `onNicknameEditClick should open nickname dialog`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("테스터")

        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameEditClick)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isEditNicknameDialogOpen)
    }

    @Test
    fun `onNicknameChange should update nickname in dialog state`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameEditClick)
        advanceUntilIdle()

        // When
        val newNickname = "새닉네임"
        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameChange(newNickname))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(newNickname, state.nicknameDialogState.nickname)
        assertFalse(state.nicknameDialogState.isError)
    }

    @Test
    fun `onSaveNickname should save nickname and close dialog`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameEditClick)
        advanceUntilIdle()

        val newNickname = "새로운닉네임"
        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameChange(newNickname))
        advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.sendEvent(HomeContract.HomeEvent.OnSaveNickname)
            advanceUntilIdle()

            // Then
            val effect = awaitItem()
            assertTrue(effect is HomeContract.HomeEffect.ShowSnackBar)
            verify(sharePreferenceRepository).saveNickname(newNickname)

            val state = viewModel.uiState.value
            assertFalse(state.isEditNicknameDialogOpen)
        }
    }

    @Test
    fun `onTagClicked should emit NavigateToResult effect`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머", "개그"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            val testTag = "유머"
            viewModel.sendEvent(HomeContract.HomeEvent.OnTagClicked(testTag))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is HomeContract.HomeEffect.NavigateToResult)
            assertEquals(testTag, (effect as HomeContract.HomeEffect.NavigateToResult).keyword)
        }
    }

    @Test
    fun `onJokeBtnClicked should emit NavigateToResult effect with default text`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(HomeContract.HomeEvent.OnJokeBtnClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is HomeContract.HomeEffect.NavigateToResult)
            assertEquals("농담하기", (effect as HomeContract.HomeEffect.NavigateToResult).keyword)
        }
    }

    @Test
    fun `onNicknameDialogDismiss should close dialog`() = runTest {
        // Given
        val testKeyword = KeywordResponse(keywords = listOf("유머"))
        whenever(homeRepository.getKeyword()).thenReturn(flowOf(Resource.Success(testKeyword)))
        whenever(sharePreferenceRepository.getCurrentSelectedCharacterIndex()).thenReturn(Config.GILL_GILL_MON)
        whenever(sharePreferenceRepository.getNickname()).thenReturn("")

        viewModel = HomeViewModel(context, homeRepository, sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameEditClick)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEditNicknameDialogOpen)

        // When
        viewModel.sendEvent(HomeContract.HomeEvent.OnNicknameDialogDismiss)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isEditNicknameDialogOpen)
    }
}
