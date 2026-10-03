//package com.example.learningdashboard.presentation.dashboard
//
//
//import androidx.lifecycle.viewModelScope
//import com.example.learningdashboard.core.base.BaseViewModel
//import com.example.learningdashboard.core.network.NetworkResult
//import com.example.learningdashboard.domain.usecase.GetCoursesUseCase
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class DashboardViewModel(
//    private val getCoursesUseCase: GetCoursesUseCase
//) : BaseViewModel() {
//
//    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Idle)
//    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()
//
//    fun loadCourses() {
//        viewModelScope.launch {
//            getCoursesUseCase().collect { result ->
//                _uiState.value = when (result) {
//                    is NetworkResult.Loading -> DashboardState.Loading
//                    is NetworkResult.Success -> {
//                        if (result.data.isEmpty()) DashboardState.Empty
//                        else DashboardState.Success(result.data)
//                    }
//                    is NetworkResult.Error -> DashboardState.Error(result.message)
//                }
//            }
//        }
//    }
//}
package com.example.learningdashboard.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.core.base.BaseViewModel
import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.data.local.dao.UserDao
import com.example.learningdashboard.data.local.entity.UserEntity
import com.example.learningdashboard.domain.usecase.GetCoursesUseCase
import com.example.learningdashboard.domain.usecase.GetProfileUseCase
import com.example.learningdashboard.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    private val _userState = MutableStateFlow<UserEntity?>(null)
    val userState: StateFlow<UserEntity?> = _userState.asStateFlow()

    // Single-event stream for one-time actions like navigation on logout
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()
    init {
        observeUserProfile()
    }

    fun loadCourses() {
        viewModelScope.launch {
            getCoursesUseCase().collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> DashboardState.Loading
                    is NetworkResult.Success -> {
                        if (result.data.isEmpty()) DashboardState.Empty
                        else DashboardState.Success(result.data)
                    }
                    is NetworkResult.Error -> DashboardState.Error(result.message)
                }
            }
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            getProfileUseCase().collectLatest { user ->
                _userState.value = user
            }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            saveProfileUseCase(name, email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Optional: clear user or auth tokens
            // userDao.clearUser()
            // 2. Emit event to navigate user back to Login screen
            _logoutEvent.emit(Unit)

        }
    }
}
