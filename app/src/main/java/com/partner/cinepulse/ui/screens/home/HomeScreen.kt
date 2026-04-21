package com.partner.cinepulse.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.AccentGold
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.EmeraldGreen
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary

// ── Colour tokens ──────────────────────────────────────────────────────────────
//private val BgDark        = Color(0xFF080C14)
//private val CardDark      = Color(0xFF0F1623)
//private val CardBorder    = Color(0xFF1C2333)
//private val AccentRed     = Color(0xFFE50914)
//private val AccentBlue    = Color(0xFF1A6BFF)
//private val AccentGold    = Color(0xFFFFB300)
//private val AccentGreen   = Color(0xFF00C853)
//private val TextPrimary   = Color(0xFFFFFFFF)
//private val TextSecondary = Color(0xFF8A95A8)

// ── Data models ────────────────────────────────────────────────────────────────
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

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToDiscussions: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onProfileClick : () -> Unit
) {
    val trendingPeople = listOf(
        TrendingPerson("Nolan",    listOf(Color(0xFFB05C1A), Color(0xFF6B3A10))),
        TrendingPerson("Zimmer",   listOf(Color(0xFF1A1A2E), Color(0xFF4158D0))),
        TrendingPerson("Chastain", listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))),
        TrendingPerson("Murphy",   listOf(Color(0xFF1565C0), Color(0xFF42A5F5))),
    )

    val activityFeed: List<ActivityItem> = listOf(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopBar(
            onProfileClick = onProfileClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // ── Hero banner ───────────────────────────────────────────────────
            item {
                HeroBanner()
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
                        text = "Trending Pulse",
                        color = TextPrimary,
                        fontSize = 17.sp,
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
                    text = "Latest Activity",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Activity feed ─────────────────────────────────────────────────
            items(activityFeed) { item ->
                when (item) {
                    is ActivityItem.ReviewActivity    -> ReviewActivityCard(item)
                    is ActivityItem.DiscussionActivity -> DiscussionActivityCard(item)
                    is ActivityItem.CommentActivity   -> CommentActivityCard(item)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ── Hero Banner ────────────────────────────────────────────────────────────────
@Composable
private fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7B3F00), Color(0xFF1A0A00))
                )
            )
    ) {
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
            HeroBadge(text = "TRENDING NOW", color = AccentBlue)
            HeroBadge(text = "NEW RELEASE",  color = AccentBlue)
        }

        // Bottom info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "ASTRAL ECHO",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "2026 • Sci-Fi Thriller • 148 min",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Dot indicators
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == 0) 20.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (i == 0) AccentBlue else TextSecondary.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(person.gradientColors))
                    .border(2.dp, Color(0xFF1C2333), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = person.name.first().toString(), color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            // Green "+" badge
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen )
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↑", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = person.name,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
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
        // Critic header row
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
                    Text(text = item.criticName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(item.badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = item.badge, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = item.timeAgo, color = TextSecondary, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(15.dp))
                Text(text = "${item.rating} / 5", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = item.movieTitle, color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item.headline, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = item.body, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(14.dp))

        // Read Full Review button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AccentBlue)
                .clickable { }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "READ FULL REVIEW", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentBlue))
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

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onNavigateToSearch = {},
        onNavigateToChatbot = {},
        onNavigateToReviews = {},
        onNavigateToDiscussions = {},
        onProfileClick = {}
    )
}