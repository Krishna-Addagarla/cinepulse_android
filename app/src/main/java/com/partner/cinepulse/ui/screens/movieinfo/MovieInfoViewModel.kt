package com.partner.cinepulse.ui.screens.movieinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MovieUiState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
)

@HiltViewModel
class MovieInfoViewModel @Inject constructor(
    val contentRepository: ContentRepository
) : ViewModel(){

    private val _uistate = MutableStateFlow(MovieUiState())
    val uiState : StateFlow<MovieUiState> = _uistate

    private val _movieInfo = MutableStateFlow<movieResponse?>(null)
    val movieInfo: StateFlow<movieResponse?> = _movieInfo

    private val _actorInfo = MutableStateFlow<actorResponse?>(null)
    val actorInfo: StateFlow<actorResponse?> = _actorInfo

    fun getMovieDetails(id : Int){

        viewModelScope.launch {
            contentRepository.getMovie(id).collect { result->
                when(result){
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true,errorMessage = null)
                    }

                    is Resource.Success -> {
                        result.data.let { data->
                           _movieInfo.value = data
                        }

                        _uistate.update {
                            it.copy(isLoading = false,errorMessage = null)
                        }
                    }

                    is Resource.Error<*> -> _uistate.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }

            }
        }
    }

    fun getActorDetails(id: Int){

        viewModelScope.launch {
            contentRepository.getActor(id).collect { result->
                when(result){
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true)
                    }

                    is Resource.Success -> {
                        result.data.let { data->
                            _actorInfo.value = data
                        }

                        _uistate.update {
                            it.copy(isLoading = false,errorMessage = null)
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