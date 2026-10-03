package com.example.learningdashboard.domain.model


data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val isCompleted: Boolean
)