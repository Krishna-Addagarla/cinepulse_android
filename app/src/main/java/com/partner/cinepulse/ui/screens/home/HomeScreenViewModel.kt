package com.partner.cinepulse.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val contentRepository: ContentRepository
) : ViewModel(){

    private val _uistate = MutableStateFlow(HomeUiState())
    val uiState : StateFlow<HomeUiState> = _uistate

    private val _inTheaterList = MutableStateFlow<List<movieResponse>>(emptyList())
    val inTheaterList: StateFlow<List<movieResponse>> = _inTheaterList

    init {
        getMoviesInTheaters()
    }
    fun getMoviesInTheaters(){

        viewModelScope.launch {
            contentRepository.getMoviesInTheaters().collect { result->
                when(result){
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true,errorMessage = null)
                    }

                    is Resource.Success -> {
                        result.data.let{data->
                            _inTheaterList.value = data

                        }
                        _uistate.update {
                            it.copy(isLoading = false,errorMessage = null)
                        }
                    }

                    is Resource.Error<*> -> _uistate.update {
                        it.copy(isLoading = false,errorMessage = result.message)
                    }
                }
            }
        }
    }

}