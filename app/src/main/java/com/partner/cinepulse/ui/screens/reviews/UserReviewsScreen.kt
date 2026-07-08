package com.partner.cinepulse.ui.screens.reviews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.partner.cinepulse.data.remote.models.ExploreActivityResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.*
import com.partner.cinepulse.utils.Resource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.repository.PostRepository
import com.partner.cinepulse.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserReviewsViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _reviewsState = MutableStateFlow<Resource<List<ExploreActivityResponse>>>(Resource.Loading())
    val reviewsState: StateFlow<Resource<List<ExploreActivityResponse>>> = _reviewsState.asStateFlow()

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            authRepository.getProfile().collect { profileResult ->
                if (profileResult is Resource.Success) {
                    val userId = profileResult.data.id
                    postRepository.getUserReviews(userId).collect { result ->
                        _reviewsState.value = result
                    }
                } else if (profileResult is Resource.Error) {
                    _reviewsState.value = Resource.Error(profileResult.message ?: "Failed to get profile")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviewsScreen(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    viewModel: UserReviewsViewModel = hiltViewModel()
) {
    val reviewsState by viewModel.reviewsState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(
                title = "Your Reviews",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BgDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgDark)
        ) {
            when (val state = reviewsState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentBlue
                    )
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message ?: "Failed to load reviews",
                            color = AccentRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadReviews() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is Resource.Success -> {
                    val list = state.data ?: emptyList()
                    if (list.isEmpty()) {
                        Text(
                            text = "You haven't written any reviews yet.",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(list) { review ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (review.movie_id != null) {
                                                onMovieClick(review.movie_id)
                                            } else if (review.tvshow_id != null) {
                                                onTvShowClick(review.tvshow_id)
                                            }
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // User Avatar representation
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(AccentBlue.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "👤",
                                            fontSize = 16.sp
                                        )
                                    }

                                    // Chat bubble card
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                                            .background(Color(0xDC0F1623))
                                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = review.movie_title ?: review.tvshow_title ?: "Review",
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = AccentGold,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "${review.rating}",
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = review.review_text ?: "",
                                                color = TextSecondary,
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = review.time_ago,
                                                color = TextSecondary.copy(alpha = 0.5f),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
