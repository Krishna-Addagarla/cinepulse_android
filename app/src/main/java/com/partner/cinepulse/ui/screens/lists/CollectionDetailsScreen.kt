package com.partner.cinepulse.ui.screens.lists

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.CollectionResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.*
import com.partner.cinepulse.utils.Resource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _collectionState = MutableStateFlow<Resource<CollectionResponse>>(Resource.Loading())
    val collectionState: StateFlow<Resource<CollectionResponse>> = _collectionState.asStateFlow()

    fun loadCollection(collectionId: Int) {
        viewModelScope.launch {
            contentRepository.viewCollection(collectionId).collect { result ->
                _collectionState.value = result
            }
        }
    }

    fun removeItem(collectionId: Int, movieId: Int?, tvShowId: Int?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            contentRepository.removeCollectionItem(collectionId, movieId, tvShowId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadCollection(collectionId)
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Failed to remove item")
                    }
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    collectionId: Int,
    collectionName: String,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    viewModel: CollectionDetailsViewModel = hiltViewModel()
) {
    val collectionState by viewModel.collectionState.collectAsStateWithLifecycle()

    var deleteError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(collectionId) {
        viewModel.loadCollection(collectionId)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = collectionName,
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
            when (val state = collectionState) {
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
                            text = state.message ?: "Failed to load collection items",
                            color = AccentRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCollection(collectionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is Resource.Success -> {
                    val list = state.data?.items ?: emptyList()
                    if (list.isEmpty()) {
                        Text(
                            text = "No items in this collection",
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
                            items(list) { item ->
                                val title = item.movie?.title ?: item.tv_show?.name ?: "Unknown"
                                val posterUrl = item.movie?.photo_url ?: item.tv_show?.photo_url ?: ""
                                val rating = item.movie?.overall_rating ?: item.tv_show?.overall_rating?.toDouble() ?: 0.0

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (item.movie_id != null) {
                                                onMovieClick(item.movie_id)
                                            } else if (item.tv_show_id != null) {
                                                onTvShowClick(item.tv_show_id)
                                            }
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xDC0F1623)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val validPosterUrl = posterUrl.takeIf { it.isNotEmpty() && it != "null" && it != "None" }
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp, 90.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(alpha = 0.05f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (validPosterUrl != null) {
                                                AsyncImage(
                                                    model = validPosterUrl,
                                                    contentDescription = title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Movie,
                                                    contentDescription = null,
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = title,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (item.movie_id != null) "Movie" else "Web Series",
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Rating",
                                                    tint = AccentGold,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = String.format("%.1f", rating),
                                                    color = TextPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.removeItem(
                                                    collectionId = collectionId,
                                                    movieId = item.movie_id,
                                                    tvShowId = item.tv_show_id,
                                                    onSuccess = {},
                                                    onError = { deleteError = it }
                                                )
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove",
                                                tint = AccentRed
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
