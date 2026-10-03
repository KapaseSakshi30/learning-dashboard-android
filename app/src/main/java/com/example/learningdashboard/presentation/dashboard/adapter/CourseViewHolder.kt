package com.example.learningdashboard.presentation.dashboard.adapter


import androidx.recyclerview.widget.RecyclerView
import com.example.learningdashboard.databinding.ItemCourseBinding
import com.example.learningdashboard.domain.model.Course

class CourseViewHolder(
    private val binding: ItemCourseBinding,
    private val onCourseClick: (Course) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(course: Course) {
        binding.tvCourseTitle.text = course.title
        binding.tvInstructor.text = "Instructor: ${course.instructor}"
        binding.pbProgress.progress = course.progressPercentage
        binding.tvProgressText.text = "${course.progressPercentage}%"
        binding.tvLessonsCount.text = "${course.totalLessons} Lessons"

        binding.btnContinue.setOnClickListener { onCourseClick(course) }
        binding.root.setOnClickListener { onCourseClick(course) }
    }
}