package com.example.learningdashboard.presentation.login

import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.core.base.BaseViewModel
import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> _state.value = LoginState.Success
                is NetworkResult.Error -> _state.value = LoginState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}