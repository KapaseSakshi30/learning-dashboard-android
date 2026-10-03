package com.example.learningdashboard.presentation.details.adapter


import androidx.recyclerview.widget.RecyclerView
import com.example.learningdashboard.databinding.ItemLessonBinding
import com.example.learningdashboard.domain.model.Lesson

class LessonViewHolder(
    private val binding: ItemLessonBinding,
    private val onLessonToggle: (Lesson) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lesson: Lesson) {
        binding.tvLessonTitle.text = lesson.title
        binding.cbCompleted.setOnCheckedChangeListener(null)
        binding.cbCompleted.isChecked = lesson.isCompleted

        binding.cbCompleted.setOnClickListener {
            onLessonToggle(lesson)
        }
    }
}