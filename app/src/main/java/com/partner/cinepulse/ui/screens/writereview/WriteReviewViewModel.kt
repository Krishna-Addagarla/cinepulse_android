package com.partner.cinepulse.ui.screens.writereview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.performanceRating
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val isLoading    : Boolean = false,
    val errorMessage : String? = null,
    val isSubmitted  : Boolean = false   // ← true on success → use to navigate away
)

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState

    private val _movieState = MutableStateFlow<Resource<movieResponse>>(Resource.Loading())
    val movieState = _movieState.asStateFlow()

    // ── Load movie ─────────────────────────────────────────────────────────────
    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            contentRepository.getMovie(movieId).collect { _movieState.value = it }
        }
    }

    // ── Build & post review ────────────────────────────────────────────────────
    /**
     * Assembles a [reviewRequest] from the form data collected in the UI
     * and calls [postReview].
     *
     * @param movieId       the movie being reviewed
     * @param overallRating star rating (1–5) converted to Double
     * @param reviewText    user's written review
     * @param castRatings   map of creditId → star rating for cast members
     * @param crewRatings   map of creditId → star rating for crew members
     */
    fun submitReview(
        movieId       : Int,
        overallRating : Float,
        reviewText    : String,
        castRatings   : Map<Int, Float>,
        crewRatings   : Map<Int, Float>,
    ) {
        val movie   = (_movieState.value as? Resource.Success)?.data ?: return
        val credits = movie.credits

        val castPerformance = castRatings
            .filter { it.value > 0f }
            .mapNotNull { (creditId, rating) ->
                val credit = credits.find { it.id == creditId } ?: return@mapNotNull null
                performanceRating(
                    artist_id   = credit.id,
                    role_id     = credit.role.id,   // ← Role.id from your Credit model
                    person_name = credit.name,
                    person_type = "cast",
                    rating      = rating.toDouble()
                )
            }

        val crewPerformance = crewRatings
            .filter { it.value > 0f }
            .mapNotNull { (creditId, rating) ->
                val credit = credits.find { it.id == creditId } ?: return@mapNotNull null
                performanceRating(
                    artist_id   = credit.id,
                    role_id     = credit.role.id,   // ← same
                    person_name = credit.name,
                    person_type = "crew",
                    rating      = rating.toDouble()
                )
            }

        val request = reviewRequest(
            rating              = overallRating.toDouble(),
            review_text         = reviewText,
            performance_ratings = castPerformance + crewPerformance
        )

        postReview(movieId, request)
    }

    // ── Post review ────────────────────────────────────────────────────────────
    fun postReview(movieId: Int, request: reviewRequest) {
        viewModelScope.launch {
            contentRepository.postReview(movieId, request).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, isSubmitted = true, errorMessage = null)
                    }
                    is Resource.Error   -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}