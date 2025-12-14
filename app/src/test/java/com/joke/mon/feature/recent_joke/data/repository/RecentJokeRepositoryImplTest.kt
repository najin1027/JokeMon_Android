package com.joke.mon.feature.recent_joke.data.repository

import com.joke.mon.feature.recent_joke.data.source.local.dao.RecentJokeDao
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * RecentJokeRepositoryImpl의 단위 테스트
 *
 * 테스트 항목:
 * - 최근 농담 목록 조회
 * - 최근 농담 추가 (제한 포함)
 * - 최근 농담 삽입
 * - 오래된 농담 정리
 * - 키로 최근 농담 조회
 */
class RecentJokeRepositoryImplTest {

    private lateinit var recentJokeDao: RecentJokeDao
    private lateinit var recentJokeRepository: RecentJokeRepository

    @Before
    fun setup() {
        recentJokeDao = mock()
        recentJokeRepository = RecentJokeRepositoryImpl(recentJokeDao)
    }

    @Test
    fun `getRecentJokes should return flow of recent jokes`() = runTest {
        // Given
        val testJokes = listOf(
            createTestRecentJoke(id = 1, key = "joke1", content = "Recent joke 1"),
            createTestRecentJoke(id = 2, key = "joke2", content = "Recent joke 2"),
            createTestRecentJoke(id = 3, key = "joke3", content = "Recent joke 3")
        )
        whenever(recentJokeDao.getRecentJokes()).thenReturn(flowOf(testJokes))

        // When
        val result = recentJokeRepository.getRecentJokes().first()

        // Then
        assertEquals(3, result.size)
        assertEquals("joke1", result[0].key)
        assertEquals("Recent joke 1", result[0].content)
        assertEquals("joke2", result[1].key)
        assertEquals("joke3", result[2].key)
        verify(recentJokeDao).getRecentJokes()
    }

    @Test
    fun `getRecentJokes should return empty list when no jokes`() = runTest {
        // Given
        whenever(recentJokeDao.getRecentJokes()).thenReturn(flowOf(emptyList()))

        // When
        val result = recentJokeRepository.getRecentJokes().first()

        // Then
        assertTrue(result.isEmpty())
        verify(recentJokeDao).getRecentJokes()
    }

    @Test
    fun `addRecentJokeWithLimit should call dao method`() = runTest {
        // Given
        val testJoke = createTestRecentJoke(key = "newJoke", content = "New recent joke")

        // When
        recentJokeRepository.addRecentJokeWithLimit(testJoke)

        // Then
        verify(recentJokeDao).addRecentJokeWithLimit(testJoke)
    }

    @Test
    fun `insertRecentJoke should call dao insert method`() = runTest {
        // Given
        val testJoke = createTestRecentJoke(key = "insertJoke", content = "Inserted joke")

        // When
        recentJokeRepository.insertRecentJoke(testJoke)

        // Then
        verify(recentJokeDao).insertRecentJoke(testJoke)
    }

    @Test
    fun `cleanupOldJokes should call dao cleanup method`() = runTest {
        // When
        recentJokeRepository.cleanupOldJokes()

        // Then
        verify(recentJokeDao).cleanupOldJokes()
    }

    @Test
    fun `getRecentJokeByKey should return joke when exists`() = runTest {
        // Given
        val jokeKey = "joke1"
        val expectedJoke = createTestRecentJoke(id = 1, key = jokeKey, content = "Test content")
        whenever(recentJokeDao.getRecentJokeByKey(jokeKey)).thenReturn(expectedJoke)

        // When
        val result = recentJokeRepository.getRecentJokeByKey(jokeKey)

        // Then
        assertNotNull(result)
        assertEquals(jokeKey, result?.key)
        assertEquals("Test content", result?.content)
        verify(recentJokeDao).getRecentJokeByKey(jokeKey)
    }

    @Test
    fun `getRecentJokeByKey should return null when joke does not exist`() = runTest {
        // Given
        val jokeKey = "nonExistentJoke"
        whenever(recentJokeDao.getRecentJokeByKey(jokeKey)).thenReturn(null)

        // When
        val result = recentJokeRepository.getRecentJokeByKey(jokeKey)

        // Then
        assertNull(result)
        verify(recentJokeDao).getRecentJokeByKey(jokeKey)
    }

    @Test
    fun `getRecentJokes should return jokes in correct order`() = runTest {
        // Given
        val testJokes = listOf(
            createTestRecentJoke(id = 3, key = "joke3", createdAt = "2025-12-12"),
            createTestRecentJoke(id = 1, key = "joke1", createdAt = "2025-12-14"),
            createTestRecentJoke(id = 2, key = "joke2", createdAt = "2025-12-13")
        )
        whenever(recentJokeDao.getRecentJokes()).thenReturn(flowOf(testJokes))

        // When
        val result = recentJokeRepository.getRecentJokes().first()

        // Then
        assertEquals(3, result.size)
        // Order should be maintained as returned by DAO
        assertEquals("joke3", result[0].key)
        assertEquals("joke1", result[1].key)
        assertEquals("joke2", result[2].key)
    }

    @Test
    fun `multiple calls to getRecentJokes should return updated data`() = runTest {
        // Given
        val firstBatch = listOf(
            createTestRecentJoke(id = 1, key = "joke1")
        )
        val secondBatch = listOf(
            createTestRecentJoke(id = 1, key = "joke1"),
            createTestRecentJoke(id = 2, key = "joke2")
        )

        whenever(recentJokeDao.getRecentJokes())
            .thenReturn(flowOf(firstBatch))
            .thenReturn(flowOf(secondBatch))

        // When
        val firstResult = recentJokeRepository.getRecentJokes().first()
        val secondResult = recentJokeRepository.getRecentJokes().first()

        // Then
        assertEquals(1, firstResult.size)
        assertEquals(2, secondResult.size)
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
