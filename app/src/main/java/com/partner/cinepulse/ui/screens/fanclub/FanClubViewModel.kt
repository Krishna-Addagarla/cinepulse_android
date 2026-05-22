package com.partner.cinepulse.ui.screens.fanclub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.data.remote.models.createFanClub
import com.partner.cinepulse.data.remote.models.createFanClubResponse
import com.partner.cinepulse.data.remote.models.imageUploadResponse
import com.partner.cinepulse.data.repository.FanClubRepository
import com.partner.cinepulse.data.repository.helperRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

// Separate loading flags so list-fetch and create never clobber each other.
data class FanClubUiState(
    val isListLoading   : Boolean = false,
    val isSubmitting    : Boolean = false,
    val errorMessage    : String? = null,
) {
    // Convenience: the Create button should be disabled while either operation is in flight.
    val isLoading: Boolean get() = isListLoading || isSubmitting
}

@HiltViewModel
class FanClubViewModel @Inject constructor(
    private val fanClubRepository: FanClubRepository,
    private val helperRepository : helperRepository
) : ViewModel() {

    private val _uistate = MutableStateFlow(FanClubUiState())
    val uistate: StateFlow<FanClubUiState> = _uistate

    private val _userFanClubs = MutableStateFlow<List<FanClubResponse>>(emptyList())
    val userFanClubs: StateFlow<List<FanClubResponse>> = _userFanClubs

    // One-shot upload result — reset to null after the screen consumes it.
    private val _uploadImage = MutableStateFlow<imageUploadResponse?>(null)
    val uploadImage: StateFlow<imageUploadResponse?> = _uploadImage

    // One-shot navigation event delivered via a Channel so it fires exactly once,
    // even if the collector recomposes or the screen is briefly paused.
    private val _navigateBack = Channel<Unit>(Channel.BUFFERED)
    val navigateBack = _navigateBack.receiveAsFlow()

    init {
        getUserFanClubs()
    }

    fun getUserFanClubs() {
        viewModelScope.launch {
            fanClubRepository.getUserFanClubs().collect { result ->
                when (result) {
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isListLoading = true, errorMessage = null)
                    }
                    is Resource.Success    -> {
                        _userFanClubs.value = result.data
                        _uistate.update { it.copy(isListLoading = false) }
                    }
                    is Resource.Error<*>   -> _uistate.update {
                        it.copy(isListLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun createFanClub(createFanClub: createFanClub) {
        viewModelScope.launch {
            fanClubRepository.createFanClub(createFanClub).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isSubmitting = true, errorMessage = null)
                    }
                    is Resource.Success    -> {
                        _uistate.update { it.copy(isSubmitting = false) }
                        // Deliver the navigation event — the screen will act on it once.
                        _navigateBack.send(Unit)
                    }
                    is Resource.Error<*>   -> _uistate.update {
                        it.copy(isSubmitting = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            helperRepository.uploadImage(image).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isSubmitting = true, errorMessage = null)
                    }
                    is Resource.Success    -> {
                        _uploadImage.value = result.data
                        _uistate.update { it.copy(isSubmitting = false) }
                    }
                    is Resource.Error<*>   -> _uistate.update {
                        it.copy(isSubmitting = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    /** Call this after the screen has consumed the upload URL so the flow can reset. */
    fun clearUploadImage() {
        _uploadImage.value = null
    }
}