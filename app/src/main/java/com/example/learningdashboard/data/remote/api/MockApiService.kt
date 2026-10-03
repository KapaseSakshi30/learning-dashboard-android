//package com.example.learningdashboard.data.remote.api
//
//
//import com.example.learningdashboard.core.util.Constants
//import com.example.learningdashboard.data.remote.dto.CourseDto
//import com.example.learningdashboard.data.remote.dto.LessonDto
//import kotlinx.coroutines.delay
//
//class MockApiService {
//    private val mockCourses = mutableListOf(
//        CourseDto(
//            id = 1,
//            title = "Python Programming",
//            instructor = "John Smith",
//            progress = 50,
//            lessons = 4,
//            lessonItems = listOf(
//                LessonDto(101, "Introduction to Python", true),
//                LessonDto(102, "Variables & Data Types", true),
//                LessonDto(103, "Functions & Modules", false),
//                LessonDto(104, "Object-Oriented Programming", false)
//            )
//        ),
//        CourseDto(
//            id = 2,
//            title = "Generative AI",
//            instructor = "Sarah Williams",
//            progress = 50,
//            lessons = 4,
//            lessonItems = listOf(
//                LessonDto(201, "Intro to LLMs", true),
//                LessonDto(202, "Prompt Engineering Basics", true),
//                LessonDto(203, "Fine-Tuning Techniques", false),
//                LessonDto(204, "Deploying AI Models", false)
//            )
//        ),
//        CourseDto(
//            id = 3,
//            title = "Full Stack Development",
//            instructor = "David Brown",
//            progress = 25,
//            lessons = 4,
//            lessonItems = listOf(
//                LessonDto(301, "HTML & CSS Fundamentals", true),
//                LessonDto(302, "JavaScript Essentials", false),
//                LessonDto(303, "Backend APIs with Node", false),
//                LessonDto(304, "Database Integration", false)
//            )
//        )
//    )
//
//    suspend fun fetchCourses(): List<CourseDto> {
//        delay(Constants.NETWORK_DELAY_MS)
//        return mockCourses
//    }
//}
package com.example.learningdashboard.data.remote.api

import com.example.learningdashboard.core.util.Constants
import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.data.remote.dto.LessonDto
import kotlinx.coroutines.delay

class MockApiService {
    private val mockCourses = mutableListOf(
        CourseDto(
            id = 1,
            title = "Python Programming",
            instructor = "John Smith",
            progress = 50,
            lessons = 4,
            lessonItems = listOf(
                LessonDto(101, "Introduction to Python", true),
                LessonDto(102, "Variables & Data Types", true),
                LessonDto(103, "Functions & Modules", false),
                LessonDto(104, "Object-Oriented Programming", false)
            )
        ),
        CourseDto(
            id = 2,
            title = "Generative AI",
            instructor = "Sarah Williams",
            progress = 50,
            lessons = 4,
            lessonItems = listOf(
                LessonDto(201, "Intro to LLMs", true),
                LessonDto(202, "Prompt Engineering Basics", true),
                LessonDto(203, "Fine-Tuning Techniques", false),
                LessonDto(204, "Deploying AI Models", false)
            )
        ),
        CourseDto(
            id = 3,
            title = "Full Stack Development",
            instructor = "David Brown",
            progress = 25,
            lessons = 4,
            lessonItems = listOf(
                LessonDto(301, "HTML & CSS Fundamentals", true),
                LessonDto(302, "JavaScript Essentials", false),
                LessonDto(303, "Backend APIs with Node", false),
                LessonDto(304, "Database Integration", false)
            )
        )
    )

    suspend fun fetchCourses(): List<CourseDto> {
        delay(Constants.NETWORK_DELAY_MS)
        return mockCourses
    }

    // Call this whenever a lesson is toggled so the Mock API reflects changes
    suspend fun updateLessonStatus(courseId: Int, lessonId: Int, isCompleted: Boolean) {
        delay(300) // Simulate fast network write operation

        val courseIndex = mockCourses.indexOfFirst { it.id == courseId }
        if (courseIndex != -1) {
            val course = mockCourses[courseIndex]
            val updatedLessons = course.lessonItems?.map { lesson ->
                if (lesson.id == lessonId) lesson.copy(isCompleted = isCompleted) else lesson
            }

            // Dynamically recalculate progress percentage
            val completedCount = updatedLessons?.count { it.isCompleted } ?: 0
            val totalCount = updatedLessons?.size ?: 1
            val newProgress = (completedCount * 100) / totalCount

            // Update in-memory list
            mockCourses[courseIndex] = course.copy(
                progress = newProgress,
                lessonItems = updatedLessons
            )
        }
    }
}
