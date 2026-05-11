package com.partner.cinepulse.ui.screens.home

import androidx.compose.animation.animateContentSize
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
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.AccentGold
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
        overall_rating = 0.0f,
        total_ratings = 0.0f,
        genres = listOf(genre),
        cast = emptyList(),
        crew = emptyList(),
        awards = emptyList()
    )
}

private val sampleTrendingMovies = listOf(
    createSampleMovie("ECHO", 2016, 148, "Sci-Fi"),
    createSampleMovie("NOVA", 2026, 132, "Action"),
    createSampleMovie("DARK MERIDIAN", 2026, 115, "Horror")
)

private val sampleTrendingPeople = listOf(
    TrendingPerson("Nolan",    listOf(Color(0xFFB05C1A), Color(0xFF6B3A10))),
    TrendingPerson("Zimmer",   listOf(Color(0xFF1A1A2E), Color(0xFF4158D0))),
    TrendingPerson("Chastain", listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))),
    TrendingPerson("Murphy",   listOf(Color(0xFF1565C0), Color(0xFF42A5F5))),
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
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val moviesInTheaters by viewModel.inTheaterList.collectAsStateWithLifecycle()
    HomeScreenContent(
        trendingPeople = sampleTrendingPeople,
        activityFeed   = sampleActivityFeed,
        onProfileClick = onProfileClick,
        onMovieClick = onMovieClick,
    moviesInTheaters
    )
}

// ── Stateless content (previewable) ───────────────────────────────────────────
@Composable
private fun HomeScreenContent(
    trendingPeople: List<TrendingPerson>,
    activityFeed: List<ActivityItem>,
    onProfileClick: () -> Unit,
    onMovieClick : (id : Int) -> Unit,
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
                HeroBanner(newMovies,onMovieClick)
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
                    items(trendingPeople) { person ->
                        TrendingPersonItem(person = person)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Latest Activity header ────────────────────────────────────────
            item {
                Text(
                    text       = "Latest Activity",
                    color      = TextPrimary,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Activity feed ─────────────────────────────────────────────────
            items(activityFeed) { item ->
                when (item) {
                    is ActivityItem.ReviewActivity     -> ReviewActivityCard(item)
                    is ActivityItem.DiscussionActivity -> DiscussionActivityCard(item)
                    is ActivityItem.CommentActivity    -> CommentActivityCard(item)
                }
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

    val currentMovies = if (isNewRelease) newRelease else sampleTrendingMovies
    val pagerState    = rememberPagerState(pageCount = { currentMovies.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Background gradient — follows current page
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable{
//                    val id = currentMovies[pagerState.currentPage].id
//                    onMovieClick(id)
//                }
//        )
        AsyncImage(
            model = currentMovies[pagerState.currentPage].photo_url,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    val id = currentMovies[pagerState.currentPage].id
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = moviesList[page].release_year.toString()+" "+moviesList[page].genres+" "+moviesList[page].runtime_minutes,
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

// ── Trending Person ────────────────────────────────────────────────────────────
@Composable
private fun TrendingPersonItem(person: TrendingPerson) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(person.gradientColors))
                    .border(2.dp, Color(0xFF1C2333), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = person.name.first().toString(),
                    color      = TextPrimary,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold
                )
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
        Text(text = person.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Activity Cards ─────────────────────────────────────────────────────────────
@Composable
private fun ReviewActivityCard(item: ActivityItem.ReviewActivity) {
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
                    .background(AccentGold),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.criticEmoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text       = item.criticName,
                        color      = TextPrimary,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(item.badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text       = item.badge,
                            color      = Color.Black,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(text = item.timeAgo, color = TextSecondary, fontSize = 11.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint     = AccentGold,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text       = "${item.rating} / 5",
                    color      = TextPrimary,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = item.movieTitle, color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item.headline, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = item.body, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AccentBlue)
                .clickable { }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text          = "READ FULL REVIEW",
                color         = TextPrimary,
                fontSize      = 13.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun DiscussionActivityCard(item: ActivityItem.DiscussionActivity) {
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
                    .background(item.userBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.userEmoji, fontSize = 20.sp)
            }
            Column {
                Text(text = item.username, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = item.timeAgo, color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AccentBlue)
            )
            Text(text = item.tag, color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = item.body, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun CommentActivityCard(item: ActivityItem.CommentActivity) {
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
                    .background(item.userBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.userEmoji, fontSize = 20.sp)
            }
            Column {
                Text(text = item.username, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = item.timeAgo, color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = item.body, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item.hashtags.forEach { tag ->
                Text(text = tag, color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        trendingPeople = sampleTrendingPeople,
        activityFeed   = sampleActivityFeed,
        onProfileClick = {},
        onMovieClick ={},
        sampleTrendingMovies
    )
}