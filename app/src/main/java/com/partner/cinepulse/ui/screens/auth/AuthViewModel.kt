package com.partner.cinepulse.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.loginRequest
import com.partner.cinepulse.data.remote.models.otpVerificationRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.data.remote.models.verifyResponse
import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------- UI State ----------

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Registration
    val registrationSuccess: Boolean = false,

    // Login / OTP verify result
    val verifyResponse: verifyResponse? = null,

    // OTP resend
    val resendOtpSuccess: Boolean = false
)

// ---------- ViewModel ----------

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Register ─────────────────────────────────────────────────────────────

    fun registerUser(request: registrationRequest) {
        viewModelScope.launch {
            authRepository.registerUser(request).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, registrationSuccess = true)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    fun loginUser(request: loginRequest) {
        viewModelScope.launch {
            authRepository.loginUser(request).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, verifyResponse = result.data)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    // ── Verify OTP ────────────────────────────────────────────────────────────

    fun verifyOTP(request: otpVerificationRequest) {
        viewModelScope.launch {
            authRepository.verifyOTP(request).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, verifyResponse = result.data)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    // ── Resend OTP ────────────────────────────────────────────────────────────

    fun resendOTP(email: String) {
        viewModelScope.launch {
            authRepository.resendOTP(email).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null, resendOtpSuccess = false)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, resendOtpSuccess = true)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetRegistrationState() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }

    fun resetResendOtpState() {
        _uiState.update { it.copy(resendOtpSuccess = false) }
    }

    fun resetVerifyResponse() {
        _uiState.update { it.copy(verifyResponse = null) }
    }
}