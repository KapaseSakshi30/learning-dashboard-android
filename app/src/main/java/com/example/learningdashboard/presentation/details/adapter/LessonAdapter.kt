package com.example.learningdashboard.presentation.details.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.learningdashboard.databinding.ItemLessonBinding
import com.example.learningdashboard.domain.model.Lesson

class LessonAdapter(
    private val onLessonToggle: (Lesson) -> Unit
) : ListAdapter<Lesson, LessonViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding, onLessonToggle)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean = oldItem == newItem
    }
}