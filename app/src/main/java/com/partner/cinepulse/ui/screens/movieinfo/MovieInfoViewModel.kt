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
    val contentRepository: ContentRepository
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
                    }
                    is Resource.Error<*> -> _uistate.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
        loadReviews(id)
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