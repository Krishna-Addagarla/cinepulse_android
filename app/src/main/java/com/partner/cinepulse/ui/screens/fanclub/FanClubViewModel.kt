package com.partner.cinepulse.ui.screens.fanclub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.data.repository.FanClubRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class FanClubUiState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
)
@HiltViewModel
class FanClubViewModel @Inject constructor(
    private val fanClubRepository: FanClubRepository
): ViewModel(){

    private val _uistate = MutableStateFlow(FanClubUiState())
    val uistate : StateFlow<FanClubUiState> = _uistate

    private val _userFanClubs = MutableStateFlow<List<FanClubResponse>>(emptyList())
    val userFanClubs : StateFlow<List<FanClubResponse>> = _userFanClubs

    init {
        getUserFanClubs()
    }

    fun getUserFanClubs(){
        viewModelScope.launch {
            fanClubRepository.getUserFanClubs().collect { result ->
                when(result){
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true,errorMessage = null)
                    }

                    is Resource.Success -> {
                        result.data.let { data ->
                           _userFanClubs.value = data
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
