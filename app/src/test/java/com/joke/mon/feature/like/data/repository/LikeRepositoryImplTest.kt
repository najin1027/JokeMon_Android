package com.joke.mon.feature.like.data.repository

import com.joke.mon.feature.like.data.source.local.dao.LikeJokeDao
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
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
 * LikeRepositoryImpl의 단위 테스트
 *
 * 테스트 항목:
 * - 즐겨찾기 농담 목록 조회
 * - 농담 추가
 * - 농담 삭제
 * - 키로 농담 삭제
 * - 농담 좋아요 상태 확인
 * - 키로 농담 조회
 */
class LikeRepositoryImplTest {

    private lateinit var likeJokeDao: LikeJokeDao
    private lateinit var likeRepository: LikeRepository

    @Before
    fun setup() {
        likeJokeDao = mock()
        likeRepository = LikeRepositoryImpl(likeJokeDao)
    }

    @Test
    fun `getLikedJokesFlow should return flow of liked jokes`() = runTest {
        // Given
        val testJokes = listOf(
            createTestLikeJoke(id = 1, key = "joke1", content = "Test joke 1"),
            createTestLikeJoke(id = 2, key = "joke2", content = "Test joke 2")
        )
        whenever(likeJokeDao.getLikedJokes()).thenReturn(flowOf(testJokes))

        // When
        val result = likeRepository.getLikedJokesFlow().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("joke1", result[0].key)
        assertEquals("Test joke 1", result[0].content)
        assertEquals("joke2", result[1].key)
        verify(likeJokeDao).getLikedJokes()
    }

    @Test
    fun `getLikedJokesFlow should return empty list when no jokes`() = runTest {
        // Given
        whenever(likeJokeDao.getLikedJokes()).thenReturn(flowOf(emptyList()))

        // When
        val result = likeRepository.getLikedJokesFlow().first()

        // Then
        assertTrue(result.isEmpty())
        verify(likeJokeDao).getLikedJokes()
    }

    @Test
    fun `addJoke should call dao insert method`() = runTest {
        // Given
        val testJoke = createTestLikeJoke(key = "newJoke", content = "New funny joke")

        // When
        likeRepository.addJoke(testJoke)

        // Then
        verify(likeJokeDao).insertJoke(testJoke)
    }

    @Test
    fun `deleteJoke should call dao delete method`() = runTest {
        // Given
        val testJoke = createTestLikeJoke(id = 1, key = "joke1", content = "Test joke")

        // When
        likeRepository.deleteJoke(testJoke)

        // Then
        verify(likeJokeDao).deleteJoke(testJoke)
    }

    @Test
    fun `deleteJokeByKey should call dao deleteJokeByKey method`() = runTest {
        // Given
        val jokeKey = "joke123"

        // When
        likeRepository.deleteJokeByKey(jokeKey)

        // Then
        verify(likeJokeDao).deleteJokeByKey(jokeKey)
    }

    @Test
    fun `isJokeLiked should return true when joke exists`() = runTest {
        // Given
        val jokeKey = "joke1"
        whenever(likeJokeDao.isJokeLiked(jokeKey)).thenReturn(flowOf(true))

        // When
        val result = likeRepository.isJokeLiked(jokeKey).first()

        // Then
        assertTrue(result)
        verify(likeJokeDao).isJokeLiked(jokeKey)
    }

    @Test
    fun `isJokeLiked should return false when joke does not exist`() = runTest {
        // Given
        val jokeKey = "nonExistentJoke"
        whenever(likeJokeDao.isJokeLiked(jokeKey)).thenReturn(flowOf(false))

        // When
        val result = likeRepository.isJokeLiked(jokeKey).first()

        // Then
        assertFalse(result)
        verify(likeJokeDao).isJokeLiked(jokeKey)
    }

    @Test
    fun `getJokeByKey should return joke when exists`() = runTest {
        // Given
        val jokeKey = "joke1"
        val expectedJoke = createTestLikeJoke(id = 1, key = jokeKey, content = "Test content")
        whenever(likeJokeDao.getJokeByKey(jokeKey)).thenReturn(expectedJoke)

        // When
        val result = likeRepository.getJokeByKey(jokeKey)

        // Then
        assertNotNull(result)
        assertEquals(jokeKey, result?.key)
        assertEquals("Test content", result?.content)
        verify(likeJokeDao).getJokeByKey(jokeKey)
    }

    @Test
    fun `getJokeByKey should return null when joke does not exist`() = runTest {
        // Given
        val jokeKey = "nonExistentJoke"
        whenever(likeJokeDao.getJokeByKey(jokeKey)).thenReturn(null)

        // When
        val result = likeRepository.getJokeByKey(jokeKey)

        // Then
        assertNull(result)
        verify(likeJokeDao).getJokeByKey(jokeKey)
    }

    /**
     * 테스트용 LikeJoke 객체 생성 헬퍼 함수
     */
    private fun createTestLikeJoke(
        id: Int = 0,
        key: String,
        content: String,
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
