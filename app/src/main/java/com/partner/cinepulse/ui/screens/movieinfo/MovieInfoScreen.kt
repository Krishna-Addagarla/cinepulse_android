@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.partner.cinepulse.ui.screens.movieinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.Credit
import com.partner.cinepulse.data.remote.models.Review
import com.partner.cinepulse.data.remote.models.Role
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.AccentRed
import com.partner.cinepulse.ui.theme.AccentGold
import com.partner.cinepulse.ui.theme.AccentGreen
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.ChipBorder
import com.partner.cinepulse.ui.theme.DarkSlate
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary
import com.partner.cinepulse.utils.formatBirthDate

// ── Data models ────────────────────────────────────────────────────────────────
data class CastMember(
    val name: String,
    val role: String,
    val score: Float,
    val emoji: String,
    val bgColors: List<Color>
)

data class UserReview(
    val id: String,
    val initials: String,
    val avatarColor: Color,
    val name: String,
    val stars: Int,
    val body: String,
    val likes: Int,
    val comments: Int
)

// ── Sample data ────────────────────────────────────────────────────────────────
private val castMembers = listOf(
    CastMember("Marcus Chen",      "Lead Actor",      9.2f, "🎭", listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))),
    CastMember("Sarah Williams",   "Lead Actress",    9.5f, "🌟", listOf(Color(0xFF880E4F), Color(0xFFC2185B))),
    CastMember("David Rodriguez",  "Director",        8.8f, "🎬", listOf(Color(0xFF1A237E), Color(0xFF303F9F))),
    CastMember("Emily Zhang",      "Cinematographer", 9.0f, "📷", listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))),
)

private val sampleReviews = listOf(
    UserReview("1", "A", Color(0xFF6A1B9A), "Alex Thompson",    5, "An absolute masterpiece! The cinematography is stunning and the performances are top-notch. This is what modern action thrillers should aspire to be.", 124, 18),
    UserReview("2", "J", Color(0xFF1565C0), "Jessica Martinez", 4, "Great plot with unexpected twists. Some pacing issues in the second act, but overall a thrilling experience that keeps you on the edge of your seat.", 89, 12),
    UserReview("3", "R", Color(0xFF2E7D32), "Ryan Park",        5, "Absolutely loved every minute. The action sequences were breathtaking and the story had real emotional depth. A must-watch!", 67, 9),
)

// ── Fake data for Preview ──────────────────────────────────────────────────────
private val previewMovieInfo = movieResponse(
    title = "Interstellar",
    plot = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival. Breathtaking visuals meet profound storytelling in this science-fiction epic.",
    photo_url = "",
    release_date = "2014-11-07",
    credits = listOf(
        Credit(
            id = 1,
            name = "Matthew McConaughey",
            character_name = "Cooper",
            photo_url = "",
            rating = 9.0,
            role = Role(
                name = "",
                id = 1
            ),
            job_detail = null,
            total_ratings = 8
        ),
        Credit(
            id = 2,
            name = "Anne Hathaway",
            character_name = "Brand",
            photo_url = "",
            rating = 8.0,
            role = Role(
                name = "",
                id = 1
            ),
            job_detail = null,
            total_ratings = 8
        ),
        Credit(
            id = 3,
            name = "Christopher Nolan",
            character_name = "Director",
            photo_url = "",
            rating = 10.0,
            role = Role(
                name = "",
                id = 1
            ),
            job_detail = null,
            total_ratings = 8
        ),
    ),
    release_year = 2012,
    runtime_minutes = 160,
    id = 1,
    overall_rating = 8.0,
    total_ratings = 8,
    genres = emptyList(),
    awards = emptyList()
)

// ── Outer screen (ViewModel-aware) ─────────────────────────────────────────────
@Composable
fun MovieInfoScreen(
    onNavigateBack: () -> Unit = {},
    id: Int,
    onArtistClick: (artistId: Int) -> Unit,
    onWriteReviewClick: (movieId: Int) -> Unit,
    viewModel: MovieInfoViewModel = hiltViewModel()
) {
    val movieInfo by viewModel.movieInfo.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        viewModel.getMovieDetails(id)
    }

    MovieInfoContent(
        onNavigateBack = onNavigateBack,
        movieInfo = movieInfo,
        uiState = uiState,
        isFavorite = isFavorite,
        collections = collections,
        onFavoriteToggle = { viewModel.toggleFavorite(id) },
        onCollectionItemToggle = { colId, isAdding -> viewModel.toggleCollectionItem(colId, id, isAdding) },
        onCreateCollection = { name, onSuccess -> viewModel.createCollection(name, onSuccess) },
        onArtistClick = onArtistClick,
        onWriteReviewClick = onWriteReviewClick,
        onLoadNextPage = { viewModel.loadNextReviewPage() }
    )
}

// ── Inner content composable (preview-safe, no ViewModel) ──────────────────────
@Composable
fun MovieInfoContent(
    onNavigateBack: () -> Unit = {},
    movieInfo: movieResponse?,
    uiState: MovieUiState,
    isFavorite: Boolean,
    collections: List<com.partner.cinepulse.data.remote.models.CollectionResponse>,
    onFavoriteToggle: () -> Unit,
    onCollectionItemToggle: (Int, Boolean) -> Unit,
    onCreateCollection: (String, () -> Unit) -> Unit,
    onArtistClick: (artistId: Int) -> Unit,
    onWriteReviewClick: (movieId: Int) -> Unit,
    onLoadNextPage: () -> Unit
) {
    var reviewFilter by remember { mutableStateOf("Latest") }
    val likedStates = remember { androidx.compose.runtime.snapshots.SnapshotStateMap<Int, Boolean>() }

    var showListsSheet by remember { mutableStateOf(false) }
    var showCreateListDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // 1. Poster hero with overlapping title + chips
            item { PosterHero(onNavigateBack = onNavigateBack, movieInfo = movieInfo) }

            // 2. Rating bar and quick actions
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    StarRatingRow(rating = movieInfo?.overall_rating?:0.0)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Favorite Button
                        androidx.compose.material3.Button(
                            onClick = onFavoriteToggle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFavorite) AccentRed.copy(alpha = 0.15f) else CardDark,
                                contentColor = if (isFavorite) AccentRed else TextPrimary
                            ),
                            border = BorderStroke(1.dp, if (isFavorite) AccentRed else CardBorder),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFavorite) "Favorited" else "Favorite",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Add to List Button
                        androidx.compose.material3.Button(
                            onClick = { showListsSheet = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CardDark,
                                contentColor = TextPrimary
                            ),
                            border = BorderStroke(1.dp, CardBorder),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to List", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // 3. Movie Plot
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    SectionLabel("Movie Plot")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text       = movieInfo?.plot ?: "",
                        color      = TextSecondary,
                        fontSize   = 14.sp,
                        lineHeight = 22.sp
                    )
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // 4. Cast & Crew
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    SectionLabel(
                        text     = "Top Cast & Crew",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        movieInfo?.credits?.forEach { credit ->
                            CastAvatar(member = credit, onArtistClick = onArtistClick)
                        }
                    }
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // 5. Reviews header with filter chips
            item {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel("User Reviews", modifier = Modifier.weight(1f))
                    FilterChip("Latest",     selected = reviewFilter == "Latest")     { reviewFilter = "Latest" }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip("Most Liked", selected = reviewFilter == "Most Liked") { reviewFilter = "Most Liked" }
                }
            }

            items(uiState.reviews, key = { it.id }) { review ->
                val isLiked = likedStates[review.id] ?: false
                ReviewCard(
                    review    = review,
                    isLiked   = isLiked as Boolean,
                    onLike    = {
                        val current = likedStates[review.id] ?: false
                        likedStates[review.id] = !current
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 7. Pagination footer
            item {
                when {
                    uiState.isReviewsLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = AccentBlue,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    !uiState.isLastReviewPage && uiState.reviews.isNotEmpty() -> {
                        // Trigger next page when this item becomes visible
                        LaunchedEffect(Unit) { onLoadNextPage() }
                    }
                    uiState.isLastReviewPage -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No more reviews", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        } // end LazyColumn

        // ── Floating "Write a Review" button ──────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A6BFF), Color(0xFF0D47A1))
                    )
                )
                .clickable {onWriteReviewClick(movieInfo?.id ?: 0) }
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment        = Alignment.CenterVertically,
            horizontalArrangement    = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "✏️", fontSize = 16.sp)
            Text(
                text       = "Write a Review",
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (showListsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showListsSheet = false },
                containerColor = CardDark,
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add to List",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                newListName = ""
                                showCreateListDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create New List",
                                tint = AccentBlue
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (collections.isEmpty()) {
                        Text(
                            text = "No lists found. Click + to create one.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                        ) {
                            items(collections) { collection ->
                                val isChecked = collection.items.any { item ->
                                    item.movie_id == movieInfo?.id || item.tv_show_id == movieInfo?.id
                                }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onCollectionItemToggle(collection.id, !isChecked)
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            onCollectionItemToggle(collection.id, !isChecked)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = AccentBlue,
                                            uncheckedColor = TextSecondary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = collection.name,
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateListDialog) {
            AlertDialog(
                onDismissRequest = { showCreateListDialog = false },
                title = { Text("Create New List", color = TextPrimary) },
                text = {
                    OutlinedTextField(
                        value = newListName,
                        onValueChange = { newListName = it },
                        label = { Text("List Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newListName.trim().isNotEmpty()) {
                                onCreateCollection(newListName.trim()) {
                                    showCreateListDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateListDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = CardDark
            )
        }

    } // end outer Box
}

// ── Poster Hero ────────────────────────────────────────────────────────────────
@Composable
private fun PosterHero(onNavigateBack: () -> Unit, movieInfo: movieResponse?) {
    Box(modifier = Modifier.fillMaxWidth()) {

        AsyncImage(
            model          = movieInfo?.photo_url,
            contentDescription = null,
            modifier       = Modifier
                .fillMaxWidth()
                .height(420.dp),
            contentScale   = ContentScale.FillBounds
        )

        // Bottom scrim — fades poster into BgDark
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(colors = listOf(Color.Transparent, BgDark))
                )
        )

        // Title + chips pinned to the bottom of the hero
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
        ) {
            Text(
                text       = movieInfo?.title ?: "",
                color      = TextPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(formatBirthDate(movieInfo?.release_date ?: ""))
                InfoChip("Action/Thriller")
            }
        }

        // Back button
        Box(
            modifier = Modifier
                .padding(top = 48.dp, start = 14.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .align(Alignment.TopStart)
                .clickable { onNavigateBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint               = TextPrimary,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

// ── Info chip ──────────────────────────────────────────────────────────────────
@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSlate)
            .border(1.dp, ChipBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = TextSecondary, fontSize = 12.sp)
    }
}

// ── Star rating bar ────────────────────────────────────────────────────────────
@Composable
private fun StarRatingRow(rating: Double) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { i ->
            Icon(
                imageVector        = if (i < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint               = if (i < rating) AccentGold else TextSecondary,
                modifier           = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = rating.toString(), color = AccentGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = " / 5", color = TextSecondary, fontSize = 14.sp)
    }
}

// ── Section label ──────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        color      = TextPrimary,
        fontSize   = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier   = modifier
    )
}

// ── Cast avatar ────────────────────────────────────────────────────────────────
@Composable
private fun CastAvatar(member: Credit, onArtistClick: (artistId: Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.width(76.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .clickable { onArtistClick(member.id) }
                    .border(2.dp, CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model              = member.photo_url,
                    contentDescription = member.name,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
            }
            // Score badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(DarkSlate)
                    .border(1.dp, CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = member.rating.toInt().toString(),
                    color      = AccentGreen,
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text       = member.name,
            color      = TextPrimary,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines   = 2,
            lineHeight = 15.sp
        )
        Text(
            text     = member.character_name,
            color    = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

// ── Filter chip ────────────────────────────────────────────────────────────────
@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AccentBlue else DarkSlate)
            .border(1.dp, if (selected) Color.Transparent else ChipBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text       = text,
            color      = if (selected) TextPrimary else TextSecondary,
            fontSize   = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ── Review card ────────────────────────────────────────────────────────────────
@Composable
private fun ReviewCard(
    review: Review,       // your API model
    isLiked: Boolean,
    onLike: () -> Unit
) {
    // Avatar color derived from user_id so it's stable
    val avatarColor = remember(review.user_id) {
        val colors = listOf(
            Color(0xFF6A1B9A), Color(0xFF1565C0),
            Color(0xFF2E7D32), Color(0xFF880E4F),
            Color(0xFF1A237E), Color(0xFF4A148C)
        )
        colors[review.user_id % colors.size]
    }
    val initials = review.user_name
        .substringBefore("@")
        .take(2)
        .uppercase()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(
                    text = review.user_name.substringBefore("@"),
                    color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < review.rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (i < review.rating) AccentGold else TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = review.review_text, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.clickable { onLike() }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) AccentRed else TextSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatBirthDate(review.created_at),
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

// ── Previews ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun MovieInfoContentPreview() {
    MovieInfoContent(
        onNavigateBack = {},
        movieInfo = previewMovieInfo,
        onArtistClick = {},
        onWriteReviewClick = {},
        uiState = MovieUiState(),
        isFavorite = false,
        collections = emptyList(),
        onFavoriteToggle = {},
        onCollectionItemToggle = { _, _ -> },
        onCreateCollection = { _, _ -> },
        onLoadNextPage = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14, name = "Null state")
@Composable
fun MovieInfoContentNullPreview() {
    MovieInfoContent(
        onNavigateBack = {},
        movieInfo = null,
        onArtistClick = {},
        onWriteReviewClick = {},
        uiState = MovieUiState(),
        isFavorite = false,
        collections = emptyList(),
        onFavoriteToggle = {},
        onCollectionItemToggle = { _, _ -> },
        onCreateCollection = { _, _ -> },
        onLoadNextPage = {}
    )
}