package com.partner.cinepulse.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private  val contentRepository: ContentRepository
): ViewModel(){

    private val _uistate = MutableStateFlow(SearchUiState())
    val uistate : StateFlow<SearchUiState> = _uistate

    private var _searchResult = MutableStateFlow<searchResponse?>(null)
    var searchResult : StateFlow<searchResponse?> = _searchResult

    fun searchContent(q: String){
        viewModelScope.launch {
            contentRepository.searchContent(q).collect { result ->
                when(result){
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true,errorMessage = null)
                    }
                    is Resource.Success ->{
                        result.data.let { data ->
                            _searchResult.value = data
                        }
                        _uistate.update{
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Error<*> -> _uistate.update {
                        it.copy(isLoading = false)
                    }
                }


            }
        }
    }


}
