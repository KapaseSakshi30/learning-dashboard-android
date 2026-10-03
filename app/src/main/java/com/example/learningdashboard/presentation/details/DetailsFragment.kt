//package com.example.learningdashboard.presentation.details
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.lifecycle.lifecycleScope
//import com.example.learningdashboard.core.base.BaseFragment
//import com.example.learningdashboard.core.util.Constants
//import com.example.learningdashboard.data.local.database.AppDatabase
//import com.example.learningdashboard.data.remote.api.MockApiService
//import com.example.learningdashboard.data.remote.repository.CourseRepositoryImpl
//import com.example.learningdashboard.databinding.FragmentDetailsBinding
//import com.example.learningdashboard.domain.usecase.GetCourseDetailsUseCase
//import com.example.learningdashboard.domain.usecase.ToggleLessonCompletionUseCase
//import com.example.learningdashboard.presentation.details.adapter.LessonAdapter
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {
//
//    private var courseId: String = ""
//
//    private val viewModel by lazy {
//        val db = AppDatabase.getInstance(requireContext())
//        val repository = CourseRepositoryImpl(MockApiService(), db.courseDao())
//        DetailsViewModel(
//            GetCourseDetailsUseCase(repository),
//            ToggleLessonCompletionUseCase(repository)
//        )
//    }
//
//    private val adapter by lazy {
//        LessonAdapter { lesson ->
//            viewModel.toggleLesson(courseId, lesson.id)
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        courseId = arguments?.getString(Constants.KEY_COURSE_ID).orEmpty()
//
//        binding.rvLessons.adapter = adapter
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.uiState.collectLatest { state ->
//                binding.progressBar.isVisible = state is DetailsState.Loading
//
//                if (state is DetailsState.Success) {
//                    binding.tvCourseTitle.text = state.course.title
//                    binding.pbProgress.progress = state.course.progressPercentage
//                    binding.tvProgressText.text = "${state.course.progressPercentage}%"
//                    adapter.submitList(state.course.lessons)
//                }
//            }
//        }
//
//        viewModel.loadCourseDetails(courseId)
//    }
//}

package com.example.learningdashboard.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learningdashboard.core.base.BaseFragment
import com.example.learningdashboard.core.util.Constants
import com.example.learningdashboard.data.local.database.AppDatabase
import com.example.learningdashboard.data.remote.api.MockApiService
import com.example.learningdashboard.data.remote.repository.CourseRepositoryImpl
import com.example.learningdashboard.databinding.FragmentDetailsBinding
import com.example.learningdashboard.domain.usecase.GetCourseDetailsUseCase
import com.example.learningdashboard.domain.usecase.ToggleLessonCompletionUseCase
import com.example.learningdashboard.presentation.details.adapter.LessonAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {

    private var courseId: String = ""

    private val viewModel by lazy {
        val db = AppDatabase.getInstance(requireContext())
        val repository = CourseRepositoryImpl(MockApiService(), db.courseDao())
        DetailsViewModel(
            GetCourseDetailsUseCase(repository),
            ToggleLessonCompletionUseCase(repository)
        )
    }

    private val adapter by lazy {
        LessonAdapter { lesson ->
            viewModel.toggleLesson(courseId, lesson.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseId = arguments?.getString(Constants.KEY_COURSE_ID).orEmpty()

        binding.rvLessons.adapter = adapter

        // Setup Done button listener to return back to Dashboard
        binding.btnDone.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.progressBar.isVisible = state is DetailsState.Loading

                if (state is DetailsState.Success) {
                    binding.tvCourseTitle.text = state.course.title
                    binding.pbProgress.progress = state.course.progressPercentage
                    binding.tvProgressText.text = "${state.course.progressPercentage}%"
                    adapter.submitList(state.course.lessons)
                }
            }
        }

        viewModel.loadCourseDetails(courseId)
    }
}