//package com.example.learningdashboard.presentation.dashboard
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.lifecycle.lifecycleScope
//import com.example.learningdashboard.R
//import com.example.learningdashboard.core.base.BaseFragment
//import com.example.learningdashboard.core.util.Constants
//import com.example.learningdashboard.data.local.database.AppDatabase
//import com.example.learningdashboard.data.remote.api.MockApiService
//import com.example.learningdashboard.data.remote.repository.CourseRepositoryImpl
//import com.example.learningdashboard.databinding.FragmentDashboardBinding
//import com.example.learningdashboard.domain.usecase.GetCoursesUseCase
//import com.example.learningdashboard.presentation.dashboard.adapter.CourseAdapter
//import com.example.learningdashboard.presentation.details.DetailsFragment
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {
//
//    private val viewModel by lazy {
//        val db = AppDatabase.getInstance(requireContext())
//        val repository = CourseRepositoryImpl(MockApiService(), db.courseDao())
//        DashboardViewModel(GetCoursesUseCase(repository))
//    }
//
//    private val adapter by lazy {
//        CourseAdapter { course ->
//            val detailsFragment = DetailsFragment().apply {
//                arguments = Bundle().apply { putString(Constants.KEY_COURSE_ID, course.id) }
//            }
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, detailsFragment)
//                .addToBackStack(null)
//                .commit()
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.rvCourses.adapter = adapter
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.uiState.collectLatest { state ->
//                binding.progressBar.isVisible = state is DashboardState.Loading
//                binding.tvEmptyState.isVisible = state is DashboardState.Empty
//                binding.tvError.isVisible = state is DashboardState.Error
//                binding.rvCourses.isVisible = state is DashboardState.Success
//
//                when (state) {
//                    is DashboardState.Success -> adapter.submitList(state.courses)
//                    is DashboardState.Error -> binding.tvError.text = state.message
//                    else -> Unit
//                }
//            }
//        }
//
//        viewModel.loadCourses()
//    }
//}


package com.example.learningdashboard.presentation.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learningdashboard.R
import com.example.learningdashboard.core.base.BaseFragment
import com.example.learningdashboard.core.util.Constants
import com.example.learningdashboard.databinding.FragmentDashboardBinding
import com.example.learningdashboard.databinding.DialogEditProfileBinding
import com.example.learningdashboard.presentation.dashboard.adapter.CourseAdapter
import com.example.learningdashboard.presentation.details.DetailsFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.navOptions
import com.example.learningdashboard.presentation.login.LoginFragment


class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    // ViewModel creation should be managed by Factory/DI or viewModels delegate
    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireContext().applicationContext)
    }

    private val adapter by lazy {
        CourseAdapter { course ->
            val detailsFragment = DetailsFragment().apply {
                arguments = Bundle().apply { putString(Constants.KEY_COURSE_ID, course.id) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detailsFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCourses.adapter = adapter

        setupToolbarAndDrawer()
        observeData()
// 1. Observe Logout Event
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutEvent.collect {
                    Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

                    // Clear the backstack so the user cannot press Back to return to Dashboard
                    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                    // Replace 'R.id.mainContainer' with the actual ID from your activity_main.xml
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LoginFragment())
                        .commit()
                }
            }
        }

        // 2. Navigation Drawer Click Listener
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }

    }

    private fun setupToolbarAndDrawer() {
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_edit_profile -> {
                    showEditProfileDialog()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    viewModel.logout()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }

    private fun observeData() {
        // Use viewLifecycleOwner.lifecycleScope with repeatOnLifecycle to prevent collection when view is stopped
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Collect UI state
                launch {
                    viewModel.uiState.collectLatest { state ->
                        binding.progressBar.isVisible = state is DashboardState.Loading
                        binding.tvEmptyState.isVisible = state is DashboardState.Empty
                        binding.tvError.isVisible = state is DashboardState.Error
                        binding.rvCourses.isVisible = state is DashboardState.Success

                        when (state) {
                            is DashboardState.Success -> adapter.submitList(state.courses)
                            is DashboardState.Error -> binding.tvError.text = state.message
                            else -> Unit
                        }
                    }
                }

                // Collect User Profile State
                launch {
                    viewModel.userState.collectLatest { user ->
                        val headerView = binding.navigationView.getHeaderView(0)
                        val tvName = headerView.findViewById<TextView>(R.id.tvNavUserName)
                        val tvEmail = headerView.findViewById<TextView>(R.id.tvNavUserEmail)

                        tvName.text = user?.name ?: "Guest User"
                        tvEmail.text = user?.email ?: "guest@example.com"
                    }
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        val currentUser = viewModel.userState.value

        dialogBinding.etName.setText(currentUser?.name ?: "John Doe")
        dialogBinding.etEmail.setText(currentUser?.email ?: "john.doe@example.com")

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val email = dialogBinding.etEmail.text.toString().trim()

                if (name.isNotEmpty() && email.isNotEmpty()) {
                    viewModel.updateProfile(name, email)
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCourses()
    }
}