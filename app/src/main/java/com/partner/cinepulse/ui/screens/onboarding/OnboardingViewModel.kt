package com.partner.cinepulse.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _userProfile = MutableStateFlow<com.partner.cinepulse.data.remote.models.userResponse?>(null)
    val userProfile: StateFlow<com.partner.cinepulse.data.remote.models.userResponse?> = _userProfile.asStateFlow()

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            authRepository.getProfile().collect { result ->
                if (result is Resource.Success) {
                    _userProfile.value = result.data
                }
            }
        }
    }

    fun savePreferences(
        region: String,
        interests: List<String>,
        languages: List<String>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            authRepository.updateProfile(region, interests, languages).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, isSuccess = true)
                        }
                        onComplete()
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}
