package com.joke.mon.feature.my_page.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * MyPageViewModel의 단위 테스트
 *
 * 테스트 항목:
 * - 초기 상태 확인
 * - 즐겨찾기 농담 메뉴 클릭
 * - 최근 농담 메뉴 클릭
 * - 캐릭터 선택 메뉴 클릭
 * - 알림 설정 메뉴 클릭
 * - 문의하기 메뉴 클릭
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MyPageViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MyPageViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `onLikedJokeClicked should emit NavigateToLikedJokes effect`() = runTest {
        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnLikedJokeClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is MyPageContract.MyPageEffect.NavigateToLikedJokes)
        }
    }

    @Test
    fun `onRecentJokeClicked should emit NavigateToRecentJokes effect`() = runTest {
        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnRecentJokeClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is MyPageContract.MyPageEffect.NavigateToRecentJokes)
        }
    }

    @Test
    fun `onNotificationSettingClicked should emit NavigateToNotificationSetting effect`() =
        runTest {
            // When & Then
            viewModel.effect.test {
                viewModel.sendEvent(MyPageContract.MyPageEvent.OnNotificationSettingClicked)
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is MyPageContract.MyPageEffect.NavigateToNotificationSetting)
            }
        }

    @Test
    fun `onSelectCharacterClicked should emit NavigateSelectCharacterScreen effect`() = runTest {
        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnSelectCharacterClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is MyPageContract.MyPageEffect.NavigateSelectCharacterScreen)
        }
    }

    @Test
    fun `onContactUsClicked should emit OpenContactEmail effect`() = runTest {
        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnContactUsClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is MyPageContract.MyPageEffect.OpenContactEmail)
        }
    }

    @Test
    fun `multiple menu clicks should emit correct effects in sequence`() = runTest {
        // When & Then
        viewModel.effect.test {
            // Click liked jokes
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnLikedJokeClicked)
            advanceUntilIdle()
            val effect1 = awaitItem()
            assertTrue(effect1 is MyPageContract.MyPageEffect.NavigateToLikedJokes)

            // Click recent jokes
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnRecentJokeClicked)
            advanceUntilIdle()
            val effect2 = awaitItem()
            assertTrue(effect2 is MyPageContract.MyPageEffect.NavigateToRecentJokes)

            // Click contact
            viewModel.sendEvent(MyPageContract.MyPageEvent.OnContactUsClicked)
            advanceUntilIdle()
            val effect3 = awaitItem()
            assertTrue(effect3 is MyPageContract.MyPageEffect.OpenContactEmail)
        }
    }

    @Test
    fun `state should not change after menu clicks`() = runTest {
        // Given
        advanceUntilIdle()
        val initialState = viewModel.uiState.value

        // When
        viewModel.sendEvent(MyPageContract.MyPageEvent.OnLikedJokeClicked)
        advanceUntilIdle()
        viewModel.sendEvent(MyPageContract.MyPageEvent.OnRecentJokeClicked)
        advanceUntilIdle()
        viewModel.sendEvent(MyPageContract.MyPageEvent.OnContactUsClicked)
        advanceUntilIdle()

        // Then - State should remain unchanged
        val finalState = viewModel.uiState.value
        assertEquals(initialState, finalState)
        assertFalse(finalState.isLoading)
    }

    @Test
    fun `all menu items should have corresponding effects`() = runTest {
        // This test ensures all menu events are handled
        val menuEvents = listOf(
            MyPageContract.MyPageEvent.OnLikedJokeClicked,
            MyPageContract.MyPageEvent.OnRecentJokeClicked,
            MyPageContract.MyPageEvent.OnNotificationSettingClicked,
            MyPageContract.MyPageEvent.OnSelectCharacterClicked,
            MyPageContract.MyPageEvent.OnContactUsClicked
        )

        // When & Then - Each event should emit an effect
        menuEvents.forEach { event ->
            viewModel.effect.test {
                viewModel.sendEvent(event)
                advanceUntilIdle()

                val effect = awaitItem()
                assertNotNull(effect)
            }
        }
    }
}
