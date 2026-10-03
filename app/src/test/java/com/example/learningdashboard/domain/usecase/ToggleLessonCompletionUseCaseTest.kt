package com.example.learningdashboard.domain.usecase


import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.domain.repository.CourseRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ToggleLessonCompletionUseCaseTest {

    private val repository: CourseRepository = mock()
    private lateinit var useCase: ToggleLessonCompletionUseCase

    @Before
    fun setUp() {
        useCase = ToggleLessonCompletionUseCase(repository)
    }

    @Test
    fun togglingLessonRecalculatesProgressCorrectly() = runBlocking {
        val courseId = "1"
        val lessonId = "101"
        val expectedCourse = Course(
            id = courseId,
            title = "Python Programming",
            instructor = "John Smith",
            progressPercentage = 50,
            totalLessons = 2,
            lessons = listOf(
                Lesson("101", courseId, "Intro", true),
                Lesson("102", courseId, "Variables", false)
            )
        )

        `when`(repository.toggleLessonCompletion(courseId, lessonId))
            .thenReturn(NetworkResult.Success(expectedCourse))

        val result = useCase(courseId, lessonId)

        assert(result is NetworkResult.Success)
        assertEquals(50, (result as NetworkResult.Success).data.progressPercentage)
    }
}