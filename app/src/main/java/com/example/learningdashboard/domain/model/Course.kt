package com.example.learningdashboard.domain.model


data class Course(
    val id: String,
    val title: String,
    val instructor: String,
    val progressPercentage: Int,
    val totalLessons: Int,
    val lessons: List<Lesson> = emptyList()
)