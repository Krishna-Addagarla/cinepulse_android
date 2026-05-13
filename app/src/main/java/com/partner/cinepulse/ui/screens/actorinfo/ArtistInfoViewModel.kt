package com.partner.cinepulse.ui.screens.actorinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArtistUiState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
)

@HiltViewModel
class ArtistInfoViewModel @Inject constructor(
    val contentRepository: ContentRepository
) : ViewModel() {

    private val _uistate = MutableStateFlow(ArtistUiState())
    val uiState: StateFlow<ArtistUiState> = _uistate

    private val _actorInfo = MutableStateFlow<actorResponse?>(null)
    val actorInfo: StateFlow<actorResponse?> = _actorInfo

    fun getActorDetails(id: Int) {
        viewModelScope.launch {
            contentRepository.getActor(id).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _actorInfo.value = result.data
                        _uistate.update {
                            it.copy(isLoading = false, errorMessage = null)
                        }
                    }
                    is Resource.Error<*> -> _uistate.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}