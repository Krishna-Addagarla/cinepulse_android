package com.partner.cinepulse.ui.screens.movieinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.Review
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reviews: List<Review> = emptyList(),
    val isReviewsLoading: Boolean = false,
    val isLastReviewPage: Boolean = false,
    val reviewError: String? = null
)

@HiltViewModel
class MovieInfoViewModel @Inject constructor(
    val contentRepository: ContentRepository,
    private val recentViewDao: com.partner.cinepulse.data.local.dao.RecentViewDao
) : ViewModel() {

    private val _uistate = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uistate

    private val _movieInfo = MutableStateFlow<movieResponse?>(null)
    val movieInfo: StateFlow<movieResponse?> = _movieInfo

    private val _actorInfo = MutableStateFlow<actorResponse?>(null)
    val actorInfo: StateFlow<actorResponse?> = _actorInfo

    private var currentMovieId: Int = -1
    private var reviewSkip = 0
    private val reviewLimit = 10
    private var isFetchingReviews = false

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _collections = MutableStateFlow<List<com.partner.cinepulse.data.remote.models.CollectionResponse>>(emptyList())
    val collections: StateFlow<List<com.partner.cinepulse.data.remote.models.CollectionResponse>> = _collections

    fun getMovieDetails(id: Int) {
        currentMovieId = id
        viewModelScope.launch {
            contentRepository.getMovie(id).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _movieInfo.value = result.data
                        _uistate.update { it.copy(isLoading = false, errorMessage = null) }
                        result.data?.let { data ->
                            viewModelScope.launch {
                                recentViewDao.addViewWithLimit(data.id, "movie", com.partner.cinepulse.utils.Constants.RECENT_VIEWS_LIMIT)
                            }
                        }
                    }
                    is Resource.Error<*> -> _uistate.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
        checkFavoriteStatus(id)
        loadCollections()
        loadReviews(id)
    }

    fun checkFavoriteStatus(movieId: Int) {
        viewModelScope.launch {
            contentRepository.checkFavoriteStatus(movieId = movieId, tvShowId = null).collect { result ->
                if (result is Resource.Success) {
                    _isFavorite.value = result.data?.is_favorite ?: false
                }
            }
        }
    }

    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                contentRepository.removeFavorite(movieId = movieId, tvShowId = null).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _isFavorite.value = false
                        }
                        is Resource.Error -> {
                            android.util.Log.e("MovieInfoViewModel", "Failed to remove favorite: ${result.message}")
                        }
                        else -> {}
                    }
                }
            } else {
                contentRepository.addFavorite(com.partner.cinepulse.data.remote.models.FavoriteAddRequest(movie_id = movieId)).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _isFavorite.value = true
                        }
                        is Resource.Error -> {
                            android.util.Log.e("MovieInfoViewModel", "Failed to add favorite: ${result.message}")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun loadCollections() {
        viewModelScope.launch {
            contentRepository.listCollections().collect { result ->
                if (result is Resource.Success) {
                    _collections.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun toggleCollectionItem(collectionId: Int, movieId: Int, isAdding: Boolean) {
        viewModelScope.launch {
            if (isAdding) {
                contentRepository.addCollectionItem(collectionId, com.partner.cinepulse.data.remote.models.CollectionItemAddRequest(movie_id = movieId)).collect { result ->
                    if (result is Resource.Success) {
                        loadCollections()
                    }
                }
            } else {
                contentRepository.removeCollectionItem(collectionId, movieId = movieId, tvShowId = null).collect { result ->
                    if (result is Resource.Success) {
                        loadCollections()
                    }
                }
            }
        }
    }

    fun createCollection(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            contentRepository.createCollection(com.partner.cinepulse.data.remote.models.CollectionCreateRequest(name)).collect { result ->
                if (result is Resource.Success) {
                    loadCollections()
                    onSuccess()
                }
            }
        }
    }

    fun loadNextReviewPage() {
        if (isFetchingReviews || _uistate.value.isLastReviewPage) return
        loadReviews(currentMovieId)
    }

    private fun loadReviews(movieId: Int) {
        if (isFetchingReviews || movieId == -1) return
        isFetchingReviews = true

        viewModelScope.launch {
            contentRepository.getMovieReview(
                movieId = movieId,
                skip = reviewSkip,
                limit = reviewLimit
            ).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> _uistate.update {
                        it.copy(isReviewsLoading = true, reviewError = null)
                    }
                    is Resource.Success -> {
                        val newReviews = result.data ?: emptyList()
                        reviewSkip += newReviews.size
                        isFetchingReviews = false
                        _uistate.update {
                            it.copy(
                                isReviewsLoading = false,
                                reviews = it.reviews + newReviews,
                                isLastReviewPage = newReviews.size < reviewLimit
                            )
                        }
                    }
                    is Resource.Error<*> -> {
                        isFetchingReviews = false
                        _uistate.update {
                            it.copy(isReviewsLoading = false, reviewError = result.message)
                        }
                    }
                }
            }
        }
    }
}