package com.joke.mon.feature.home.data.repository

import android.content.Context
import com.joke.mon.R
import com.joke.mon.core.util.Resource
import com.joke.mon.feature.home.data.source.remote.api.HomeApiService
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

/**
 * HomeRepositoryImpl의 단위 테스트
 *
 * 테스트 항목:
 * - 키워드 추천 API 성공
 * - 키워드 추천 API 실패 (네트워크 오류)
 * - 키워드 추천 API 실패 (서버 오류)
 * - 키워드 추천 API 응답 Body가 null인 경우
 */
class HomeRepositoryImplTest {

    private lateinit var homeApiService: HomeApiService
    private lateinit var context: Context
    private lateinit var homeRepository: HomeRepository

    @Before
    fun setup() {
        homeApiService = mock()
        context = mock()
        homeRepository = HomeRepositoryImpl(context, homeApiService)

        // Mock context strings
        whenever(context.getString(R.string.error_unknown)).thenReturn("알 수 없는 오류")
        whenever(context.getString(R.string.error_internet_connect)).thenReturn("인터넷 연결 오류")
    }

    @Test
    fun `getKeyword should emit Loading and Success when API call succeeds`() = runTest {
        // Given
        val testKeywords = listOf("유머", "웃긴이야기", "개그")
        val keywordResponse = KeywordResponse(keywords = testKeywords)
        val successResponse = Response.success(keywordResponse)
        whenever(homeApiService.fetchRecommendKeyword()).thenReturn(successResponse)

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Success with data
        assertTrue(results[1] is Resource.Success)
        val successResult = results[1] as Resource.Success
        assertEquals(testKeywords, successResult.data?.keywords)
    }

    @Test
    fun `getKeyword should emit Loading and Error when API returns error code`() = runTest {
        // Given
        val errorResponse = Response.error<KeywordResponse>(
            404,
            "Not Found".toResponseBody(null)
        )
        whenever(homeApiService.fetchRecommendKeyword()).thenReturn(errorResponse)

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Error
        assertTrue(results[1] is Resource.Error)
        val errorResult = results[1] as Resource.Error
        assertTrue(errorResult.message?.contains("404") == true)
    }

    @Test
    fun `getKeyword should emit Loading and Error when network IOException occurs`() = runTest {
        // Given
        whenever(homeApiService.fetchRecommendKeyword()).thenAnswer {
            throw IOException("Network error")
        }

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Error with internet connection error message
        assertTrue(results[1] is Resource.Error)
        val errorResult = results[1] as Resource.Error
        assertEquals("인터넷 연결 오류", errorResult.message)
    }

    @Test
    fun `getKeyword should emit Loading and Error when general Exception occurs`() = runTest {
        // Given
        whenever(homeApiService.fetchRecommendKeyword()).thenAnswer {
            throw RuntimeException("Unexpected error")
        }

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Error with unknown error message
        assertTrue(results[1] is Resource.Error)
        val errorResult = results[1] as Resource.Error
        assertEquals("알 수 없는 오류", errorResult.message)
    }

    @Test
    fun `getKeyword should emit Loading and Error when response body is null`() = runTest {
        // Given
        val successResponseWithNullBody = Response.success<KeywordResponse>(null)
        whenever(homeApiService.fetchRecommendKeyword()).thenReturn(successResponseWithNullBody)

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Error
        assertTrue(results[1] is Resource.Error)
        val errorResult = results[1] as Resource.Error
        assertEquals("알 수 없는 오류", errorResult.message)
    }

    @Test
    fun `getKeyword should emit Loading and Success with empty keywords list`() = runTest {
        // Given
        val emptyKeywordResponse = KeywordResponse(keywords = emptyList())
        val successResponse = Response.success(emptyKeywordResponse)
        whenever(homeApiService.fetchRecommendKeyword()).thenReturn(successResponse)

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Success but with empty list
        assertTrue(results[1] is Resource.Success)
        val successResult = results[1] as Resource.Success
        assertTrue(successResult.data?.keywords?.isEmpty() == true)
    }

    @Test
    fun `getKeyword should emit Loading and Success with multiple keywords`() = runTest {
        // Given
        val largeKeywordList = (1..10).map { "키워드$it" }
        val keywordResponse = KeywordResponse(keywords = largeKeywordList)
        val successResponse = Response.success(keywordResponse)
        whenever(homeApiService.fetchRecommendKeyword()).thenReturn(successResponse)

        // When
        val results = homeRepository.getKeyword().toList()

        // Then
        assertEquals(2, results.size)

        // First emission should be Loading
        assertTrue(results[0] is Resource.Loading)

        // Second emission should be Success with all keywords
        assertTrue(results[1] is Resource.Success)
        val successResult = results[1] as Resource.Success
        assertEquals(10, successResult.data?.keywords?.size)
        assertEquals("키워드1", successResult.data?.keywords?.first())
        assertEquals("키워드10", successResult.data?.keywords?.last())
    }
}
