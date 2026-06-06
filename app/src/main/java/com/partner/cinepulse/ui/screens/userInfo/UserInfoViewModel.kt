package com.partner.cinepulse.ui.screens.userInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.data.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val tokenRepository: TokenRepository
) : ViewModel() {

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            tokenRepository.clearTokens()
            onComplete()
        }
    }
}