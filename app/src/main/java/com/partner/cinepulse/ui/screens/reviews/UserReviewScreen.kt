package com.partner.cinepulse.ui.screens.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.reviewResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.ui.theme.*
import com.partner.cinepulse.utils.Resource
import com.partner.cinepulse.utils.formatBirthDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserReviewViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _movieState = MutableStateFlow<Resource<movieResponse>>(Resource.Loading())
    val movieState = _movieState.asStateFlow()

    private val _submitState = MutableStateFlow<Resource<reviewResponse>?>(null)
    val submitState = _submitState.asStateFlow()

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            contentRepository.getMovie(movieId).collect { result ->
                _movieState.value = result
            }
        }
    }

    fun submitReview(movieId: Int, rating: Double, reviewText: String, onSuccess: () -> Unit) {
        _submitState.value = Resource.Loading()
        viewModelScope.launch {
            val request = reviewRequest(
                rating = rating,
                review_text = reviewText,
                performance_ratings = emptyList()
            )
            contentRepository.postReview(movieId, request).collect { result ->
                _submitState.value = result
                if (result is Resource.Success) {
                    onSuccess()
                }
            }
        }
    }
}

@Composable
fun UserReviewScreen(
    onBackClick: () -> Unit,
    movieId: Int,
    viewModel: UserReviewViewModel = hiltViewModel()
) {
    val movieState by viewModel.movieState.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    UserReviewContent(
        onBackClick = onBackClick,
        movieState = movieState,
        submitState = submitState,
        onSubmit = { rating, text ->
            viewModel.submitReview(movieId, rating, text, onSuccess = onBackClick)
        }
    )
}

@Composable
fun UserReviewContent(
    onBackClick: () -> Unit,
    movieState: Resource<movieResponse>,
    submitState: Resource<reviewResponse>?,
    onSubmit: (Double, String) -> Unit
) {
    var rating by remember { mutableStateOf(4.0) }
    var reviewText by remember { mutableStateOf("") }
    var submitError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(submitState) {
        if (submitState is Resource.Error) {
            submitError = submitState.message ?: "Failed to post review"
        } else {
            submitError = null
        }
    }

    Scaffold(
        containerColor = BgDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgDark)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back Button and screen title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x8C0F1623))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Write a Review",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Movie Info Card
            when (movieState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentBlue)
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = "Failed to load movie details",
                        color = AccentRed,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
                is Resource.Success -> {
                    val movie = movieState.data
                    if (movie != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xDC0F1623))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val posterUrl = movie.photo_url.takeIf { it.isNotEmpty() && it != "null" && it != "None" }
                            Box(
                                modifier = Modifier
                                    .size(width = 60.dp, height = 90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (posterUrl != null) {
                                    AsyncImage(
                                        model = posterUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = movie.title,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Released: " + formatBirthDate(movie.release_date),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${movie.runtime_minutes} mins",
                                    color = AccentBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Rating Selection Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How would you rate this?",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Big Numeric Rating Display
                Text(
                    text = "%.1f".format(rating),
                    color = AccentGold,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )

                // Rating descriptor emoji
                val description = when {
                    rating < 1.5 -> "Terrible 😠"
                    rating < 2.5 -> "Mediocre 😐"
                    rating < 3.5 -> "Good 🙂"
                    rating < 4.5 -> "Excellent 🤩"
                    else -> "Masterpiece! 🏆"
                }
                Text(
                    text = description,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Overlapping Interactive Star Row + Slider
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toDouble() },
                        valueRange = 1f..5f,
                        steps = 7, // 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0
                        colors = SliderDefaults.colors(
                            thumbColor = AccentGold,
                            activeTrackColor = AccentGold,
                            inactiveTrackColor = Color.White.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val active = rating >= (index + 1)
                        Icon(
                            imageVector = if (active) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (active) AccentGold else Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Written Review text area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Your Review",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = { Text("Write your thoughts about the movie here...", color = TextSecondary, fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color(0xDC0F1623),
                        unfocusedContainerColor = Color(0xDC0F1623),
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)
                )

                if (submitError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = submitError!!, color = AccentRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Submit Button
                Button(
                    onClick = {
                        onSubmit(rating, reviewText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = submitState !is Resource.Loading && reviewText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        disabledContainerColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (submitState is Resource.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Submit Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
