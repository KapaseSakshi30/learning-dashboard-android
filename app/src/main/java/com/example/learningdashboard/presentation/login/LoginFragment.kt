package com.example.learningdashboard.presentation.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learningdashboard.R
import com.example.learningdashboard.core.base.BaseFragment
import com.example.learningdashboard.databinding.FragmentLoginBinding
import com.example.learningdashboard.domain.usecase.LoginUseCase
import com.example.learningdashboard.presentation.dashboard.DashboardFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel by lazy {
        LoginViewModel(LoginUseCase())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                binding.progressBar.isVisible = state is LoginState.Loading
                binding.btnLogin.isEnabled = state !is LoginState.Loading
                binding.tvError.isVisible = state is LoginState.Error

                when (state) {
                    is LoginState.Error -> binding.tvError.text = state.message
                    is LoginState.Success -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, DashboardFragment())
                            .commit()
                    }
                    else -> Unit
                }
            }
        }
    }
}