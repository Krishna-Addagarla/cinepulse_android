package com.partner.cinepulse.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.ArtistResponse
import com.partner.cinepulse.data.remote.models.ExploreActivityResponse
import com.partner.cinepulse.data.remote.models.searchItem
import com.partner.cinepulse.data.remote.models.userResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.AccentGold
import com.partner.cinepulse.ui.theme.AccentRed
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.EmeraldGreen
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary

// ── Data models ────────────────────────────────────────────────────────────────
data class HeroMovie(
    val title: String,
    val meta: String,
    val gradient: Brush
)

data class TrendingPerson(
    val name: String,
    val gradientColors: List<Color>
)

sealed class ActivityItem {
    data class ReviewActivity(
        val criticName: String,
        val criticEmoji: String,
        val badge: String,
        val badgeColor: Color,
        val timeAgo: String,
        val rating: Float,
        val movieTitle: String,
        val headline: String,
        val body: String
    ) : ActivityItem()

    data class DiscussionActivity(
        val username: String,
        val userEmoji: String,
        val userBgColor: Color,
        val timeAgo: String,
        val tag: String,
        val title: String,
        val body: String
    ) : ActivityItem()

    data class CommentActivity(
        val username: String,
        val userEmoji: String,
        val userBgColor: Color,
        val timeAgo: String,
        val body: String,
        val hashtags: List<String>
    ) : ActivityItem()
}

// ── Shared sample data ─────────────────────────────────────────────────────────
private val sampleNewMovies = listOf(
    HeroMovie(
        title    = "ASTRAL ECHO",
        meta     = "2026 • Sci-Fi Thriller • 148 min",
        gradient = Brush.verticalGradient(listOf(Color(0xFF7B3F00), Color(0xFF1A0A00)))
    ),
    HeroMovie(
        title    = "NOVA BREACH",
        meta     = "2025 • Action • 132 min",
        gradient = Brush.verticalGradient(listOf(Color(0xFF003F7B), Color(0xFF001A3A)))
    ),
    HeroMovie(
        title    = "DARK MERIDIAN",
        meta     = "2026 • Horror • 115 min",
        gradient = Brush.verticalGradient(listOf(Color(0xFF3F007B), Color(0xFF1A0033)))
    ),
)

private fun createSampleMovie(
    name: String,
    year: Int,
    runtime: Int,
    genre: String
): movieResponse {
    return movieResponse(
        title = name,
        photo_url = "",
        plot = "Sample plot for $name",
        release_date = "$year-01-01",
        release_year = year,
        runtime_minutes = runtime,
        id = name.hashCode(),
        overall_rating = 0.0,
        total_ratings = 0,
        genres = emptyList(),
        credits = emptyList(),
        awards = emptyList()
    )
}

private val sampleTrendingMovies = listOf(
    createSampleMovie("ECHO", 2016, 148, "Sci-Fi"),
    createSampleMovie("NOVA", 2026, 132, "Action"),
    createSampleMovie("DARK MERIDIAN", 2026, 115, "Horror")
)

private val sampleTrendingArtists = listOf(
    ArtistResponse(
        id = 1,
        name = "Christopher Nolan",
        photo_url = "https://image.tmdb.org/t/p/w500/xu9iaLO85r5Zzo7j16R36g5zGS6.jpg",
        overall_rating = 4.8f,
        total_ratings = 250,
        roles = emptyList(),
        created_at = ""
    ),
    ArtistResponse(
        id = 2,
        name = "Cillian Murphy",
        photo_url = "https://image.tmdb.org/t/p/w500/3eZ27x3444455g.jpg",
        overall_rating = 4.7f,
        total_ratings = 180,
        roles = emptyList(),
        created_at = ""
    ),
    ArtistResponse(
        id = 3,
        name = "Margot Robbie",
        photo_url = "https://image.tmdb.org/t/p/w500/iu9iaLO85r5Zzo7j16R36g5zGS6.jpg",
        overall_rating = 4.6f,
        total_ratings = 190,
        roles = emptyList(),
        created_at = ""
    )
)

private val sampleActivityFeed: List<ActivityItem> = listOf(
    ActivityItem.ReviewActivity(
        criticName  = "Jane D.",
        criticEmoji = "💎",
        badge       = "Diamond Critic",
        badgeColor  = Color(0xFFFFB300),
        timeAgo     = "2 hours ago",
        rating      = 4.5f,
        movieTitle  = "REVIEWS",
        headline    = "Astral Echo is a masterpiece that redefines science fiction",
        body        = "The cinematography alone is worth the price of admission. Denis Villeneuve has crafted a visual and emotional journey that stays with you long after the credits roll..."
    ),
    ActivityItem.DiscussionActivity(
        username    = "DreamWeaver42",
        userEmoji   = "🤔",
        userBgColor = Color(0xFF1565C0),
        timeAgo     = "5 hours ago",
        tag         = "ALT-PLOT",
        title       = "Different Ending for Inception",
        body        = "What if the top never wobbles, but someone else enters the dream? What if Mal was right all along and Cobb is still trapped? This alternative ending would..."
    ),
    ActivityItem.CommentActivity(
        username    = "FilmGeek88",
        userEmoji   = "🎬",
        userBgColor = Color(0xFF4A148C),
        timeAgo     = "1 day ago",
        body        = "Just rewatched Oppenheimer. Still blown away by that scene! The sound design and Cillian's performance are absolutely haunting. Nolan at his finest.",
        hashtags    = listOf("#Oppenheimer", "#Murphy", "#Nolan", "#CinePulse")
    ),
)

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToDiscussions: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onProfileClick: () -> Unit,
    onMovieClick : (id : Int) -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToCreatePost: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val moviesInTheaters by viewModel.inTheaterList.collectAsStateWithLifecycle()
    val trendingArtists by viewModel.trendingArtists.collectAsStateWithLifecycle()
    val exploreFeed by viewModel.exploreFeed.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getUserProfile { hasOnboarded ->
            if (!hasOnboarded) {
                onNavigateToOnboarding()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePost,
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = BgDark
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HomeScreenContent(
                trendingArtists = if (trendingArtists.isEmpty()) sampleTrendingArtists else trendingArtists,
                exploreFeed     = exploreFeed,
                onProfileClick  = onProfileClick,
                onMovieClick    = onMovieClick,
                onArtistClick   = { /* navigate to actor info screen */ },
                newMovies       = if (moviesInTheaters.isEmpty()) sampleTrendingMovies else moviesInTheaters
            )
        }
    }
}

// ── Stateless content (previewable) ───────────────────────────────────────────
@Composable
private fun HomeScreenContent(
    trendingArtists: List<ArtistResponse>,
    exploreFeed: List<ExploreActivityResponse>,
    onProfileClick: () -> Unit,
    onMovieClick : (id : Int) -> Unit,
    onArtistClick: (id: Int) -> Unit,
    newMovies : List<movieResponse>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopBar(onProfileClick = onProfileClick)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Hero banner ───────────────────────────────────────────────────
            item {
                HeroBanner(newMovies, onMovieClick)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── Trending Pulse ────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "📈", fontSize = 15.sp)
                    Text(
                        text       = "Trending Pulse",
                        color      = TextPrimary,
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(trendingArtists) { artist ->
                        TrendingArtistItem(artist = artist, onArtistClick = onArtistClick)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Explore feed header ───────────────────────────────────────────
            item {
                Text(
                    text       = "Explore",
                    color      = TextPrimary,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Explore activity feed ─────────────────────────────────────────
            items(exploreFeed) { activity ->
                ExploreActivityCard(activity = activity)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ── Hero Banner ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroBanner(newRelease : List<movieResponse>,onMovieClick: (Int) -> Unit) {
    // false = TRENDING NOW (default), true = NEW RELEASE
    var isNewRelease by remember { mutableStateOf(false) }

    val currentMovies = if (isNewRelease) {
        if (newRelease.isEmpty()) sampleTrendingMovies else newRelease
    } else {
        sampleTrendingMovies
    }
    val pagerState    = rememberPagerState(
        initialPage = 0,
        pageCount = { currentMovies.size.coerceAtLeast(1)}
    )
    LaunchedEffect(currentMovies.size, isNewRelease) {
        if (pagerState.currentPage >= currentMovies.size) {
            pagerState.scrollToPage(0)
        }
    }

    if (currentMovies.isEmpty()) return

    // Safe index — clamp to valid range
    val safeIndex = pagerState.currentPage.coerceIn(0, currentMovies.lastIndex)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {

        AsyncImage(
            model = currentMovies[safeIndex].photo_url,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    val id = currentMovies[safeIndex].id
                    onMovieClick(id)
                },
            contentScale = ContentScale.FillBounds
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Top badges
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeroBadge(
                text       = "TRENDING NOW",
                color      = AccentBlue,
                isSelected = !isNewRelease,
                onClick    = { isNewRelease = false }
            )
            HeroBadge(
                text       = "NEW RELEASE",
                color      = AccentBlue,
                isSelected = isNewRelease,
                onClick    = { isNewRelease = true }
            )
        }

        // Bottom swipeable section
        MoviesSection(
            moviesList = currentMovies,
            pagerState = pagerState
        )
    }
}

// ── Movies Section (BoxScope extension) ───────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.MoviesSection(
    moviesList: List<movieResponse>,
    pagerState: PagerState
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
    ) {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text          = moviesList[page].title,
                    color         = TextPrimary,
                    fontSize      = 30.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                val genresString = moviesList[page].genres.joinToString(", ") { it.name }
                val runtime = moviesList[page].runtime_minutes
                val formattedRuntime = if (runtime > 0) "${runtime / 60}h ${runtime % 60}m" else ""
                val infoText = listOfNotNull(
                    moviesList[page].release_year.toString(),
                    genresString.takeIf { it.isNotEmpty() },
                    formattedRuntime.takeIf { it.isNotEmpty() }
                ).joinToString("  •  ")

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = infoText,
                    color    = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Dot indicators synced to pager
        Row(
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(moviesList.size) { i ->
                val isSelected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .animateContentSize()
                        .size(width = if (isSelected) 20.dp else 8.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) AccentBlue
                            else TextSecondary.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

// ── Hero Badge ────────────────────────────────────────────────────────────────
@Composable
private fun HeroBadge(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color, RoundedCornerShape(20.dp))
            .background(if (isSelected) color else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text       = text,
            color      = if (isSelected) Color.White else color,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Trending Artist Item ────────────────────────────────────────────────────────
@Composable
private fun TrendingArtistItem(artist: ArtistResponse, onArtistClick: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onArtistClick(artist.id) }
    ) {
        Box {
            if (!artist.photo_url.isNullOrEmpty()) {
                AsyncImage(
                    model = artist.photo_url,
                    contentDescription = artist.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF1C2333), CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = 0.2f))
                        .border(2.dp, Color(0xFF1C2333), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = artist.name.firstOrNull()?.toString() ?: "?",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen)
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↑", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = artist.name,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

// ── Explore Activity Card ───────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreActivityCard(activity: ExploreActivityResponse) {
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                val emoji = when (activity.activity_type) {
                    "review" -> "✍️"
                    "discussion" -> "📣"
                    else -> "💬"
                }
                Text(text = emoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.user_name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = activity.time_ago,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            
            if (activity.activity_type == "review" && activity.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentGold.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = AccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = activity.rating.toString(),
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        val titleText = when (activity.activity_type) {
            "review" -> {
                if (activity.movie_title != null) "Reviewed Movie: ${activity.movie_title}"
                else if (activity.tvshow_title != null) "Reviewed Show: ${activity.tvshow_title}"
                else "Posted a review"
            }
            "discussion" -> {
                if (activity.fanclub_name != null) "Club Discussion in ${activity.fanclub_name}"
                else "Club Discussion"
            }
            else -> null
        }
        
        if (titleText != null) {
            Text(
                text = titleText,
                color = AccentBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        
        val bodyText = activity.review_text ?: activity.content
        if (!bodyText.isNullOrEmpty()) {
            Text(
                text = bodyText,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        
        if (!activity.media_urls.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activity.media_urls.forEach { url ->
                    if (url.isNotEmpty()) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Post Media",
                            modifier = Modifier
                                .height(120.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
        
        val hasTags = !activity.tagged_artists.isNullOrEmpty() ||
                      !activity.tagged_movies.isNullOrEmpty() ||
                      !activity.tagged_tvshows.isNullOrEmpty()
                      
        if (hasTags) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activity.tagged_artists?.forEach { tag ->
                    TagChip(text = "@${tag.name}", color = AccentBlue)
                }
                activity.tagged_movies?.forEach { tag ->
                    TagChip(text = "#${tag.title}", color = AccentGold)
                }
                activity.tagged_tvshows?.forEach { tag ->
                    TagChip(text = "#${tag.title}", color = AccentGold)
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Create Post Dialog with Autocomplete ───────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    currentUser: userResponse?,
    tagSuggestions: List<searchItem>,
    onDismiss: () -> Unit,
    onSearchSuggestions: (String) -> Unit,
    onSubmit: (content: String, artistIds: List<Int>, movieIds: List<Int>, tvshowIds: List<Int>) -> Unit
) {
    var text by remember { mutableStateOf("") }
    
    val taggedArtists = remember { mutableStateListOf<searchItem>() }
    val taggedMovies = remember { mutableStateListOf<searchItem>() }
    val taggedTvshows = remember { mutableStateListOf<searchItem>() }

    val isTagging = remember(text) {
        val lastWord = text.split(" ", "\n").lastOrNull() ?: ""
        lastWord.startsWith("@")
    }
    
    val tagQuery = remember(text) {
        val lastWord = text.split(" ", "\n").lastOrNull() ?: ""
        if (lastWord.startsWith("@")) lastWord.drop(1) else ""
    }

    LaunchedEffect(tagQuery) {
        if (isTagging) {
            onSearchSuggestions(tagQuery)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        containerColor = CardDark,
        tonalElevation = 6.dp,
        title = {
            Text(
                text = "Create a Post",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "You must mention/tag at least one artist, movie, or TV show using @ symbol.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("What's on your mind? Tag with @", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = BgDark,
                        unfocusedContainerColor = BgDark
                    ),
                    maxLines = 5
                )

                if (isTagging && tagSuggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp),
                        colors = CardDefaults.cardColors(containerColor = BgDark),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        LazyColumn(contentPadding = PaddingValues(8.dp)) {
                            items(tagSuggestions) { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val words = text.split(" ", "\n").toMutableList()
                                            if (words.isNotEmpty()) {
                                                words[words.lastIndex] = "@${suggestion.name} "
                                            }
                                            text = words.joinToString(" ")
                                            
                                            when (suggestion.type) {
                                                "artist", "user" -> {
                                                    if (taggedArtists.none { it.id == suggestion.id }) {
                                                        taggedArtists.add(suggestion)
                                                    }
                                                }
                                                "movie" -> {
                                                    if (taggedMovies.none { it.id == suggestion.id }) {
                                                        taggedMovies.add(suggestion)
                                                    }
                                                }
                                                "tv_show" -> {
                                                    if (taggedTvshows.none { it.id == suggestion.id }) {
                                                        taggedTvshows.add(suggestion)
                                                    }
                                                }
                                            }
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (suggestion.type) {
                                            "artist" -> "👤 ${suggestion.name}"
                                            "movie" -> "🎬 ${suggestion.name}"
                                            "tv_show" -> "📺 ${suggestion.name}"
                                            else -> "👤 ${suggestion.name}"
                                        },
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                val totalTags = taggedArtists.size + taggedMovies.size + taggedTvshows.size
                if (totalTags > 0) {
                    Text(
                        text = "Tagged entities ($totalTags):",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        taggedArtists.forEach { artist ->
                            TagDisplayChip(text = "@${artist.name}", onRemove = { taggedArtists.remove(artist) }, color = AccentBlue)
                        }
                        taggedMovies.forEach { movie ->
                            TagDisplayChip(text = "#${movie.name}", onRemove = { taggedMovies.remove(movie) }, color = AccentGold)
                        }
                        taggedTvshows.forEach { show ->
                            TagDisplayChip(text = "#${show.name}", onRemove = { taggedTvshows.remove(show) }, color = AccentGold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            val hasMinTag = taggedArtists.isNotEmpty() || taggedMovies.isNotEmpty() || taggedTvshows.isNotEmpty()
            val canPost = text.trim().isNotEmpty() && hasMinTag
            
            Button(
                onClick = {
                    onSubmit(
                        text,
                        taggedArtists.map { it.id },
                        taggedMovies.map { it.id },
                        taggedTvshows.map { it.id }
                    )
                },
                enabled = canPost,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Post", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun TagDisplayChip(text: String, onRemove: () -> Unit, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "×",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onRemove() }
        )
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        trendingArtists = emptyList(),
        exploreFeed     = emptyList(),
        onProfileClick  = {},
        onMovieClick    = {},
        onArtistClick   = {},
        newMovies       = sampleTrendingMovies
    )
}