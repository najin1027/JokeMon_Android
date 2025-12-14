package com.joke.mon.feature.search.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.joke.mon.core.data.repository.SharePreferenceRepository
import com.joke.mon.core.util.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.time.Duration.Companion.seconds
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

/**
 * SearchViewModel의 단위 테스트
 *
 * 테스트 항목:
 * - 초기 상태 확인
 * - 키워드 입력 및 버튼 활성화
 * - 카테고리 선택
 * - 검색 버튼 클릭 시 네비게이션
 * - 빈 키워드로 검색 시도
 * - 캐릭터 인덱스 관찰
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var sharePreferenceRepository: SharePreferenceRepository
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sharePreferenceRepository = mock()

        // Mock default character index
        whenever(sharePreferenceRepository.observeCurrentSelectedCharacterIndex())
            .thenReturn(flowOf(Config.GILL_GILL_MON))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // When
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("", state.keyword)
        assertEquals(0, state.selectedCategoryId)
        assertFalse(state.isActionBtnEnabled)
    }

    @Test
    fun `onKeywordChanged with valid text should enable action button`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("테스트 키워드"))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("테스트 키워드", state.keyword)
        assertTrue(state.isActionBtnEnabled)
    }

    @Test
    fun `onKeywordChanged with blank text should disable action button`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // First set valid keyword
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("테스트"))
        advanceUntilIdle()

        // When - Set blank keyword
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("   "))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("   ", state.keyword)
        assertFalse(state.isActionBtnEnabled)
    }

    @Test
    fun `onKeywordChanged with empty string should disable action button`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged(""))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.keyword)
        assertFalse(state.isActionBtnEnabled)
    }

    @Test
    fun `onCategorySelected should update selected category`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(SearchContract.SearchEvent.OnCategorySelected(categoryId = 2))
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.uiState.value.selectedCategoryId)
    }

    @Test
    fun `onCategorySelected multiple times should update correctly`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(SearchContract.SearchEvent.OnCategorySelected(categoryId = 1))
        advanceUntilIdle()
        viewModel.sendEvent(SearchContract.SearchEvent.OnCategorySelected(categoryId = 3))
        advanceUntilIdle()
        viewModel.sendEvent(SearchContract.SearchEvent.OnCategorySelected(categoryId = 2))
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.uiState.value.selectedCategoryId)
    }

    @Test
    fun `onSearchButtonClicked with valid keyword should emit NavigateToResult effect`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("유머"))
        advanceUntilIdle()
        viewModel.sendEvent(SearchContract.SearchEvent.OnCategorySelected(categoryId = 1))
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is SearchContract.SearchEffect.NavigateToResult)
            val navigateEffect = effect as SearchContract.SearchEffect.NavigateToResult
            assertEquals("유머", navigateEffect.keyword)
            assertEquals(1, navigateEffect.categoryId)
        }
    }

    @Test
    fun `onSearchButtonClicked should trim keyword before navigation`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("  유머  "))
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is SearchContract.SearchEffect.NavigateToResult)
            assertEquals("유머", (effect as SearchContract.SearchEffect.NavigateToResult).keyword)
        }
    }

    @Test
    fun `onSearchButtonClicked with blank keyword should not emit effect`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("   "))
        advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.sendEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
            advanceUntilIdle()

            // Then - Should not emit any effect
            expectNoEvents()
        }
    }

    @Test
    fun `onSearchButtonClicked with empty keyword should not emit effect`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.sendEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
            advanceUntilIdle()

            // Then - Should not emit any effect
            expectNoEvents()
        }
    }

    @Test
    fun `observeSelectedCharacterIndex should update state when character changes`() = runTest {
        // Given
        whenever(sharePreferenceRepository.observeCurrentSelectedCharacterIndex())
            .thenReturn(flowOf(Config.HEE_HEE_MON))

        // When
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // Then
        assertEquals(Config.HEE_HEE_MON, viewModel.uiState.value.selectedCharacterIndex)
    }

    @Test
    fun `search with category 0 and keyword should work`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("개그"))
        advanceUntilIdle()
        viewModel.sendEvent(SearchContract.SearchEvent.OnCategorySelected(categoryId = 0))
        advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.sendEvent(SearchContract.SearchEvent.OnSearchButtonClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is SearchContract.SearchEffect.NavigateToResult)
            val navigateEffect = effect as SearchContract.SearchEffect.NavigateToResult
            assertEquals("개그", navigateEffect.keyword)
            assertEquals(0, navigateEffect.categoryId)
        }
    }

    @Test
    fun `multiple keyword changes should update state correctly`() = runTest {
        // Given
        viewModel = SearchViewModel(sharePreferenceRepository)
        advanceUntilIdle()

        // When
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("첫번째"))
        advanceUntilIdle()
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("두번째"))
        advanceUntilIdle()
        viewModel.sendEvent(SearchContract.SearchEvent.OnKeywordChanged("세번째"))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("세번째", state.keyword)
        assertTrue(state.isActionBtnEnabled)
    }
}
