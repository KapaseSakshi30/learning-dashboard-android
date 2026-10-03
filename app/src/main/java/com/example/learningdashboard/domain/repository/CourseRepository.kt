package com.example.learningdashboard.domain.repository


import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<NetworkResult<List<Course>>>
    fun getCourseDetails(courseId: String): Flow<NetworkResult<Course>>
    suspend fun toggleLessonCompletion(courseId: String, lessonId: String): NetworkResult<Course>
}