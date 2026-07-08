package com.partner.cinepulse.ui.screens.userInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.userResponse
import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.data.repository.TokenRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.partner.cinepulse.data.repository.PostRepository
import com.partner.cinepulse.data.remote.models.PostResponse
import com.partner.cinepulse.data.remote.models.ExploreActivityResponse
import com.partner.cinepulse.data.repository.ContentRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val tokenRepository: TokenRepository,
    val postRepository: PostRepository,
    val contentRepository: ContentRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<userResponse?>(null)
    val userProfile: StateFlow<userResponse?> = _userProfile.asStateFlow()

    private val _userPosts = MutableStateFlow<List<PostResponse>>(emptyList())
    val userPosts: StateFlow<List<PostResponse>> = _userPosts.asStateFlow()

    private val _userReviews = MutableStateFlow<List<ExploreActivityResponse>>(emptyList())
    val userReviews: StateFlow<List<ExploreActivityResponse>> = _userReviews.asStateFlow()

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount.asStateFlow()

    private val _listsCount = MutableStateFlow(0)
    val listsCount: StateFlow<Int> = _listsCount.asStateFlow()

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            authRepository.getProfile().collect { result ->
                if (result is Resource.Success) {
                    _userProfile.value = result.data
                    val userId = result.data.id
                    getUserPosts(userId)
                    getUserReviews(userId)
                    getFavoritesCount()
                    getListsCount()
                }
            }
        }
    }

    private fun getFavoritesCount() {
        viewModelScope.launch {
            contentRepository.listFavorites().collect { result ->
                if (result is Resource.Success) {
                    _favoritesCount.value = result.data?.size ?: 0
                }
            }
        }
    }

    private fun getListsCount() {
        viewModelScope.launch {
            contentRepository.listCollections().collect { result ->
                if (result is Resource.Success) {
                    _listsCount.value = result.data?.size ?: 0
                }
            }
        }
    }

    private fun getUserPosts(userId: Int) {
        viewModelScope.launch {
            postRepository.getUserPosts(userId, 0, 50).collect { result ->
                if (result is Resource.Success) {
                    _userPosts.value = result.data
                }
            }
        }
    }

    private fun getUserReviews(userId: Int) {
        viewModelScope.launch {
            postRepository.getUserReviews(userId).collect { result ->
                if (result is Resource.Success) {
                    _userReviews.value = result.data
                }
            }
        }
    }

    fun checkUsername(username: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.checkUsername(username).collect { result ->
                if (result is Resource.Success) {
                    onResult(result.data.is_taken)
                }
            }
        }
    }

    fun updateProfile(username: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            authRepository.updateProfile(username = username).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _userProfile.value = result.data
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Failed to update profile")
                    }
                    else -> {}
                }
            }
        }
    }

    fun uploadAvatar(file: okhttp3.MultipartBody.Part, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            authRepository.uploadAvatar(file).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _userProfile.value = result.data
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Failed to upload avatar")
                    }
                    else -> {}
                }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            tokenRepository.clearTokens()
            onComplete()
        }
    }
}