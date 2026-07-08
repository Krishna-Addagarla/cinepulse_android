package com.partner.cinepulse.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.*
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.data.repository.PostRepository
import com.partner.cinepulse.data.repository.AuthRepository
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
    val isPostCreating: Boolean = false,
    val postCreateSuccess: Boolean = false
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val contentRepository: ContentRepository,
    val postRepository: PostRepository,
    val authRepository: AuthRepository
) : ViewModel(){

    private val _uistate = MutableStateFlow(HomeUiState())
    val uiState : StateFlow<HomeUiState> = _uistate

    private val _inTheaterList = MutableStateFlow<List<movieResponse>>(emptyList())
    val inTheaterList: StateFlow<List<movieResponse>> = _inTheaterList

    private val _trendingArtists = MutableStateFlow<List<ArtistResponse>>(emptyList())
    val trendingArtists: StateFlow<List<ArtistResponse>> = _trendingArtists

    private val _exploreFeed = MutableStateFlow<List<ExploreActivityResponse>>(emptyList())
    val exploreFeed: StateFlow<List<ExploreActivityResponse>> = _exploreFeed

    private val _tagSuggestions = MutableStateFlow<List<searchItem>>(emptyList())
    val tagSuggestions: StateFlow<List<searchItem>> = _tagSuggestions

    private val _currentUser = MutableStateFlow<userResponse?>(null)
    val currentUser: StateFlow<userResponse?> = _currentUser

    init {
        refreshAll()
    }

    fun refreshAll() {
        getMoviesInTheaters()
        getTrendingArtists()
        getExploreFeed()
        getUserProfile()
    }

    fun getMoviesInTheaters(){
        viewModelScope.launch {
            contentRepository.getMoviesInTheaters().collect { result->
                when(result){
                    is Resource.Loading -> _uistate.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _inTheaterList.value = result.data ?: emptyList()
                        _uistate.update {
                            it.copy(isLoading = false, errorMessage = null)
                        }
                    }
                    is Resource.Error -> _uistate.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun getTrendingArtists() {
        viewModelScope.launch {
            contentRepository.getTrendingArtists().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _trendingArtists.value = result.data ?: emptyList()
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

    fun getExploreFeed() {
        viewModelScope.launch {
            postRepository.getExploreFeed(skip = 0, limit = 50).collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _exploreFeed.value = result.data ?: emptyList()
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

    fun getUserProfile(onCheckComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            authRepository.getProfile().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val user = result.data
                        _currentUser.value = user
                        if (user != null) {
                            val hasOnboarded = !user.interests.isNullOrEmpty() && !user.languages.isNullOrEmpty()
                            onCheckComplete?.invoke(hasOnboarded)
                        } else {
                            onCheckComplete?.invoke(true)
                        }
                    }
                    is Resource.Error -> {
                        onCheckComplete?.invoke(true) // error fallback
                    }
                }
            }
        }
    }

    fun searchTagSuggestions(query: String) {
        if (query.isEmpty()) {
            _tagSuggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            contentRepository.searchContent(query).collect { result ->
                if (result is Resource.Success) {
                    _tagSuggestions.value = result.data?.results ?: emptyList()
                }
            }
        }
    }

    fun createPost(
        content: String,
        taggedArtistIds: List<Int>,
        taggedMovieIds: List<Int>,
        taggedTvshowIds: List<Int>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val req = PostCreateRequest(
                content = content,
                is_public = true,
                tagged_artist_ids = taggedArtistIds,
                tagged_movie_ids = taggedMovieIds,
                tagged_tvshow_ids = taggedTvshowIds
            )
            postRepository.createPost(req).collect { result ->
                when (result) {
                    is Resource.Loading -> _uistate.update {
                        it.copy(isPostCreating = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _uistate.update {
                            it.copy(isPostCreating = false, postCreateSuccess = true)
                        }
                        getExploreFeed() // Refresh feed after creation
                        onComplete()
                    }
                    is Resource.Error -> _uistate.update {
                        it.copy(isPostCreating = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun resetPostCreateState() {
        _uistate.update { it.copy(postCreateSuccess = false, errorMessage = null) }
    }
}