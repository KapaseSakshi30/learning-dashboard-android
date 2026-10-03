package com.example.learningdashboard.data.remote.repository

import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.data.local.dao.CourseDao
import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.remote.api.MockApiService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class CourseRepositoryImplTest {

    private val apiService: MockApiService = mock()
    private val courseDao: CourseDao = mock()

    @Test
    fun getCoursesEmitsCacheFirstThenRemoteSuccess() = runBlocking {
        val cached = listOf(CourseEntity("1", "Cached Course", "Instructor", 20, 5))
        `when`(courseDao.getAllCourses()).thenReturn(cached)
        `when`(apiService.fetchCourses()).thenReturn(emptyList())

        val repository = CourseRepositoryImpl(apiService, courseDao)
        val emissions = repository.getCourses().toList()

        assertTrue(emissions[0] is NetworkResult.Loading)
        assertTrue(emissions[1] is NetworkResult.Success)
        assertEquals("Cached Course", (emissions[1] as NetworkResult.Success).data[0].title)
    }
}