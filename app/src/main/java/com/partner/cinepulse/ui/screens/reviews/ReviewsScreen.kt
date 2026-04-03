package com.partner.cinepulse.ui.screens.reviews

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.partner.cinepulse.ui.theme.AccentGold
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.DarkSlate
import com.partner.cinepulse.ui.theme.SecondBlue
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary

// ── Colour tokens ──────────────────────────────────────────────────────────────
//private val BgDark        = Color(0xFF080C14)
//private val CardDark      = Color(0xFF0F1623)
//private val CardBorder    = Color(0xFF1C2333)
//private val AccentRed     = Color(0xFFE50914)
//private val AccentBlue    = Color(0xFF1565C0)
//private val AccentGold    = Color(0xFFFFB300)
//private val TextPrimary   = Color(0xFFFFFFFF)
//private val TextSecondary = Color(0xFF8A95A8)
//private val TabSelected   = Color(0xFF1A6BFF)
//private val TabUnselected = Color(0xFF141B27)

// ── Data models ────────────────────────────────────────────────────────────────
data class Critic(
    val name: String,
    val emoji: String,
    val reviews: String,
    val followers: String,
    val bgColor: Color
)

data class FeaturedReview(
    val movieTitle: String,
    val gradientTop: Color,
    val gradientBottom: Color,
    val criticName: String,
    val criticEmoji: String,
    val criticBadge: String,
    val badgeColor: Color,
    val timeAgo: String,
    val rating: Float,
    val headline: String,
    val body: String,
    val likes: Int,
    val comments: Int
)

@Composable
fun ReviewsScreen(onNavigateBack: () -> Boolean) {
    val tabs = listOf("For You", "Following", "Trending", "Top Rated")
    var selectedTab by remember { mutableStateOf(0) }

    val critics = listOf(
        Critic("Jane D.",     "💎", "342 reviews", "45.2K followers", Color(0xFF1565C0)),
        Critic("CineMaven",   "🎬", "289 reviews", "38.7K followers", Color(0xFF1565C0)),
        Critic("ReelTalk",    "🎥", "201 reviews", "27.1K followers", Color(0xFF6A1B9A)),
        Critic("FilmCritic_Mike","⚡","178 reviews", "19.4K followers", Color(0xFFE65100)),
    )

    val reviews = listOf(
        FeaturedReview(
            movieTitle    = "Oppenheimer",
            gradientTop   = Color(0xFF2C1810),
            gradientBottom= Color(0xFF1A0E08),
            criticName    = "Jane D.",
            criticEmoji   = "💎",
            criticBadge   = "Diamond Critic",
            badgeColor    = Color(0xFFFFB300),
            timeAgo       = "2 hours ago",
            rating        = 4.5f,
            headline      = "A haunting masterpiece about the cost of creation",
            body          = "Nolan delivers his most mature and introspective work yet. The sound design alone is worth the price of admission, creating an atmosphere of dread that perfectly captures Oppenheimer's internal conflict...",
            likes         = 1234,
            comments      = 89
        ),
        FeaturedReview(
            movieTitle    = "Dune: Part Two",
            gradientTop   = Color(0xFF7B3F00),
            gradientBottom= Color(0xFF3E1F00),
            criticName    = "FilmCritic_Mike",
            criticEmoji   = "⚡",
            criticBadge   = "Verified Critic",
            badgeColor    = Color(0xFF1A6BFF),
            timeAgo       = "5 hours ago",
            rating        = 4.8f,
            headline      = "Epic in every sense of the word",
            body          = "Villeneuve outdoes himself with this sequel. The world-building is immaculate, the action sequences are breathtaking, and the performances elevate an already stellar script...",
            likes         = 2456,
            comments      = 156
        ),
        FeaturedReview(
            movieTitle    = "Poor Things",
            gradientTop   = Color(0xFF1A237E),
            gradientBottom= Color(0xFF0D1245),
            criticName    = "CineMaven",
            criticEmoji   = "🎬",
            criticBadge   = "Top Critic",
            badgeColor    = Color(0xFF6A1B9A),
            timeAgo       = "1 day ago",
            rating        = 4.3f,
            headline      = "Wildly imaginative and boldly executed",
            body          = "Lanthimos crafts a visually stunning fever dream that challenges conventional storytelling. Stone's performance is unlike anything seen in recent cinema...",
            likes         = 987,
            comments      = 64
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        TopBar(
            title = "Reviews",
            showSearchIcon = false,
            showNotificationIcon = false,
            showProfileIcon = false,
            showBackButton = false
        )

        // ── Filter icon row ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardDark)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚙", fontSize = 16.sp, color = TextPrimary)
            }
        }

        // ── Tab row ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, label ->
                TabChip(
                    text = label,
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // ── Top Critics ───────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🏅", fontSize = 16.sp)
                    Text(
                        text = "Top Critics This Week",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    critics.forEach { critic ->
                        CriticCard(critic = critic)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Featured Reviews header ───────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📈", fontSize = 16.sp)
                    Text(
                        text = "Featured Reviews",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Review cards ──────────────────────────────────────────────────
            items(reviews) { review ->
                ReviewCard(review = review)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────────

@Composable
private fun TabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) SecondBlue else DarkSlate)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else CardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) TextPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun CriticCard(critic: Critic) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable {}
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(critic.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = critic.emoji, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = critic.name,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = critic.reviews,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = critic.followers,
            color = SecondBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReviewCard(review: FeaturedReview) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
    ) {
        // ── Movie banner ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(review.gradientTop, review.gradientBottom)
                    )
                )
        ) {
            // Overlay gradient for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )
            Text(
                text = review.movieTitle,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            )
        }

        // ── Review body ───────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(14.dp)) {

            // Critic row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AccentGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = review.criticEmoji, fontSize = 18.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = review.criticName,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        // Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(review.badgeColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = review.criticBadge,
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = review.timeAgo,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = review.rating.toString(),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Headline
            Text(
                text = review.headline,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Body
            Text(
                text = review.body,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Footer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "☆", color = TextSecondary, fontSize = 15.sp)
                    Text(
                        text = review.likes.toString(),
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Comments
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "💬", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        text = review.comments.toString(),
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Read Full Review button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondBlue)
                        .clickable { }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Read Full Review",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun ReviewsScreenPreview() {
    ReviewsScreen(onNavigateBack = { false })
}