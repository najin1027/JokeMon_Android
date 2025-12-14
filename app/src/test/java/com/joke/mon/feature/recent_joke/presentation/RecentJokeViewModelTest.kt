package com.joke.mon.feature.recent_joke.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.joke.mon.core.util.Config
import com.joke.mon.feature.recent_joke.data.repository.RecentJokeRepository
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
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
 * RecentJokeViewModel의 단위 테스트
 *
 * 테스트 항목:
 * - 초기 상태 및 최근 농담 로딩
 * - 정렬 옵션 변경 (최신순/오래된순)
 * - 정렬 메뉴 열기/닫기
 * - 농담 클릭 시 네비게이션
 * - 뒤로가기
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RecentJokeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var recentJokeRepository: RecentJokeRepository
    private lateinit var viewModel: RecentJokeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        recentJokeRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load recent jokes sorted by latest`() = runTest {
        // Given
        val testJokes = listOf(
            createTestRecentJoke(id = 1, key = "joke1", createdAt = "2025-12-12"),
            createTestRecentJoke(id = 2, key = "joke2", createdAt = "2025-12-14"),
            createTestRecentJoke(id = 3, key = "joke3", createdAt = "2025-12-13")
        )
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(testJokes))

        // When
        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(3, state.recentJokes.size)
        assertEquals(Config.SortOption.LATEST, state.sortOption)
        // Should be sorted by createdAt descending
        assertEquals("joke2", state.recentJokes[0].key) // 2025-12-14
        assertEquals("joke3", state.recentJokes[1].key) // 2025-12-13
        assertEquals("joke1", state.recentJokes[2].key) // 2025-12-12
    }

    @Test
    fun `initial state with empty jokes should have empty list`() = runTest {
        // Given
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.recentJokes.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `onSortSelected OLDEST should sort jokes by date ascending`() = runTest {
        // Given
        val testJokes = listOf(
            createTestRecentJoke(id = 1, key = "joke1", createdAt = "2025-12-14"),
            createTestRecentJoke(id = 2, key = "joke2", createdAt = "2025-12-12"),
            createTestRecentJoke(id = 3, key = "joke3", createdAt = "2025-12-13")
        )
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(testJokes))

        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortSelected(Config.SortOption.OLDEST))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(Config.SortOption.OLDEST, state.sortOption)
        assertEquals("joke2", state.recentJokes[0].key) // 2025-12-12
        assertEquals("joke3", state.recentJokes[1].key) // 2025-12-13
        assertEquals("joke1", state.recentJokes[2].key) // 2025-12-14
        assertFalse(state.isSortMenuVisible)
    }

    @Test
    fun `onSortSelected LATEST should sort jokes by date descending`() = runTest {
        // Given
        val testJokes = listOf(
            createTestRecentJoke(id = 1, key = "joke1", createdAt = "2025-12-12"),
            createTestRecentJoke(id = 2, key = "joke2", createdAt = "2025-12-14"),
            createTestRecentJoke(id = 3, key = "joke3", createdAt = "2025-12-13")
        )
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(testJokes))

        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // Change to OLDEST first
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortSelected(Config.SortOption.OLDEST))
        advanceUntilIdle()

        // When - Change back to LATEST
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortSelected(Config.SortOption.LATEST))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(Config.SortOption.LATEST, state.sortOption)
        assertEquals("joke2", state.recentJokes[0].key) // 2025-12-14
        assertEquals("joke3", state.recentJokes[1].key) // 2025-12-13
        assertEquals("joke1", state.recentJokes[2].key) // 2025-12-12
    }

    @Test
    fun `onSortMenuOpen should show sort menu`() = runTest {
        // Given
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(emptyList()))
        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortMenuOpen)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSortMenuVisible)
    }

    @Test
    fun `onSortMenuDismiss should hide sort menu`() = runTest {
        // Given
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(emptyList()))
        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortMenuOpen)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortMenuDismiss)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isSortMenuVisible)
    }

    @Test
    fun `onJokeClicked should emit NavigateToResultJoke effect`() = runTest {
        // Given
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(emptyList()))
        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            val testJokeKey = "joke123"
            viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnJokeClicked(testJokeKey))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is RecentJokeContract.RecentJokeEffect.NavigateToResultJoke)
            assertEquals(
                testJokeKey,
                (effect as RecentJokeContract.RecentJokeEffect.NavigateToResultJoke).jokeKey
            )
        }
    }

    @Test
    fun `onBackClicked should emit NavigateToBack effect`() = runTest {
        // Given
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(emptyList()))
        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnBackClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is RecentJokeContract.RecentJokeEffect.NavigateToBack)
        }
    }

    @Test
    fun `sort menu should close when sort option is selected`() = runTest {
        // Given
        val testJokes = listOf(createTestRecentJoke(key = "joke1"))
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(testJokes))

        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortMenuOpen)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSortMenuVisible)

        // When
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortSelected(Config.SortOption.OLDEST))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isSortMenuVisible)
    }

    @Test
    fun `sorting should work with single joke`() = runTest {
        // Given
        val testJokes = listOf(
            createTestRecentJoke(id = 1, key = "joke1", createdAt = "2025-12-14")
        )
        whenever(recentJokeRepository.getRecentJokes()).thenReturn(flowOf(testJokes))

        viewModel = RecentJokeViewModel(recentJokeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(RecentJokeContract.RecentJokeEvent.OnSortSelected(Config.SortOption.OLDEST))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.recentJokes.size)
        assertEquals("joke1", state.recentJokes[0].key)
    }

    /**
     * 테스트용 RecentJoke 객체 생성 헬퍼 함수
     */
    private fun createTestRecentJoke(
        id: Int = 0,
        key: String,
        content: String = "Test joke content",
        categoryId: Int = 1,
        createdAt: String = "2025-12-14",
        keyword: String = "테스트",
        isSaved: Boolean = false,
    ) = RecentJoke(
        id = id,
        key = key,
        content = content,
        categoryId = categoryId,
        createdAt = createdAt,
        keyword = keyword,
        isSaved = isSaved
    )
}
