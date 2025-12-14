package com.joke.mon.feature.like.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.joke.mon.core.util.Config
import com.joke.mon.feature.like.data.repository.LikeRepository
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
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
 * LikeViewModel의 단위 테스트
 *
 * 테스트 항목:
 * - 초기 상태 및 즐겨찾기 농담 로딩
 * - 카테고리 필터링
 * - 정렬 옵션 변경
 * - 농담 아이템 클릭
 * - 좋아요 토글
 * - 뒤로가기
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LikeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var likeRepository: LikeRepository
    private lateinit var viewModel: LikeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        likeRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load liked jokes`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", categoryId = 1, createdAt = "2025-12-14"),
            createTestLikeJoke(id = 2, key = "joke2", categoryId = 2, createdAt = "2025-12-13")
        )
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(testJokes))

        // When
        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.allJokes.size)
        assertEquals(2, state.filteredJokes.size)
        assertEquals(0, state.selectedCategoryId)
        assertEquals(Config.SortOption.LATEST, state.sortOption)
    }

    @Test
    fun `onCategorySelected should filter jokes by category`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", categoryId = 1, createdAt = "2025-12-14"),
            createTestLikeJoke(id = 2, key = "joke2", categoryId = 2, createdAt = "2025-12-13"),
            createTestLikeJoke(id = 3, key = "joke3", categoryId = 1, createdAt = "2025-12-12")
        )
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(testJokes))

        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnCategorySelected(categoryId = 1))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.selectedCategoryId)
        assertEquals(2, state.filteredJokes.size)
        assertTrue(state.filteredJokes.all { it.categoryId == 1 })
    }

    @Test
    fun `onCategorySelected with 0 should show all jokes`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", categoryId = 1),
            createTestLikeJoke(id = 2, key = "joke2", categoryId = 2),
            createTestLikeJoke(id = 3, key = "joke3", categoryId = 3)
        )
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(testJokes))

        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        viewModel.sendEvent(LikeContract.LikeEvent.OnCategorySelected(categoryId = 1))
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnCategorySelected(categoryId = 0))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(0, state.selectedCategoryId)
        assertEquals(3, state.filteredJokes.size)
    }

    @Test
    fun `onSortSelected LATEST should sort jokes by date descending`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", createdAt = "2025-12-12"),
            createTestLikeJoke(id = 2, key = "joke2", createdAt = "2025-12-14"),
            createTestLikeJoke(id = 3, key = "joke3", createdAt = "2025-12-13")
        )
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(testJokes))

        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnSortSelected(Config.SortOption.LATEST))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(Config.SortOption.LATEST, state.sortOption)
        assertEquals("joke2", state.filteredJokes[0].key) // 2025-12-14
        assertEquals("joke3", state.filteredJokes[1].key) // 2025-12-13
        assertEquals("joke1", state.filteredJokes[2].key) // 2025-12-12
    }

    @Test
    fun `onSortSelected OLDEST should sort jokes by date ascending`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", createdAt = "2025-12-14"),
            createTestLikeJoke(id = 2, key = "joke2", createdAt = "2025-12-12"),
            createTestLikeJoke(id = 3, key = "joke3", createdAt = "2025-12-13")
        )
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(testJokes))

        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnSortSelected(Config.SortOption.OLDEST))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(Config.SortOption.OLDEST, state.sortOption)
        assertEquals("joke2", state.filteredJokes[0].key) // 2025-12-12
        assertEquals("joke3", state.filteredJokes[1].key) // 2025-12-13
        assertEquals("joke1", state.filteredJokes[2].key) // 2025-12-14
    }

    @Test
    fun `onSortMenuOpen should show sort menu`() = runTest {
        // Given
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(emptyList()))
        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnSortMenuOpen)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSortMenuVisible)
    }

    @Test
    fun `onSortMenuDismiss should hide sort menu`() = runTest {
        // Given
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(emptyList()))
        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        viewModel.sendEvent(LikeContract.LikeEvent.OnSortMenuOpen)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnSortMenuDismiss)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isSortMenuVisible)
    }

    @Test
    fun `onItemClicked should emit NavigateToDetail effect`() = runTest {
        // Given
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(emptyList()))
        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            val testJokeKey = "joke123"
            viewModel.sendEvent(LikeContract.LikeEvent.OnItemClicked(testJokeKey))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is LikeContract.LikeEffect.NavigateToDetail)
            assertEquals(testJokeKey, (effect as LikeContract.LikeEffect.NavigateToDetail).jokeKey)
        }
    }

    @Test
    fun `onLikeToggled should delete joke from repository`() = runTest {
        // Given
        val testJoke = createTestLikeJoke(id = 1, key = "joke1")
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(listOf(testJoke)))
        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(LikeContract.LikeEvent.OnLikeToggled(testJoke))
        advanceUntilIdle()

        // Then
        verify(likeRepository).deleteJokeByKey("joke1")
    }

    @Test
    fun `onBackBtnClicked should emit NavigateBack effect`() = runTest {
        // Given
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(emptyList()))
        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(LikeContract.LikeEvent.OnBackBtnClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is LikeContract.LikeEffect.NavigateBack)
        }
    }

    @Test
    fun `filtering and sorting should work together`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", categoryId = 1, createdAt = "2025-12-12"),
            createTestLikeJoke(id = 2, key = "joke2", categoryId = 2, createdAt = "2025-12-14"),
            createTestLikeJoke(id = 3, key = "joke3", categoryId = 1, createdAt = "2025-12-15"),
            createTestLikeJoke(id = 4, key = "joke4", categoryId = 2, createdAt = "2025-12-13")
        )
        whenever(likeRepository.getLikedJokesFlow()).thenReturn(flowOf(testJokes))

        viewModel = LikeViewModel(likeRepository)
        advanceUntilIdle()

        // When - Filter by category 1 and sort by OLDEST
        viewModel.sendEvent(LikeContract.LikeEvent.OnCategorySelected(categoryId = 1))
        advanceUntilIdle()
        viewModel.sendEvent(LikeContract.LikeEvent.OnSortSelected(Config.SortOption.OLDEST))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.filteredJokes.size)
        assertTrue(state.filteredJokes.all { it.categoryId == 1 })
        assertEquals("joke1", state.filteredJokes[0].key) // 2025-12-12
        assertEquals("joke3", state.filteredJokes[1].key) // 2025-12-15
    }

    /**
     * 테스트용 LikeJoke 객체 생성 헬퍼 함수
     */
    private fun createTestLikeJoke(
        id: Int = 0,
        key: String,
        content: String = "Test joke content",
        categoryId: Int = 1,
        createdAt: String = "2025-12-14",
        keyword: String = "테스트",
        isSaved: Boolean = true,
    ) = LikeJoke(
        id = id,
        key = key,
        content = content,
        categoryId = categoryId,
        createdAt = createdAt,
        keyword = keyword,
        isSaved = isSaved
    )
}
