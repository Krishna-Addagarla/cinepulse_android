package com.partner.cinepulse.ui.screens.actorinfo

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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

// ── Colour tokens ──────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF080C14)
private val CardDark      = Color(0xFF0F1623)
private val CardBorder    = Color(0xFF1C2333)
private val ChipBorder    = Color(0xFF1E2A3A)
private val DarkSlate     = Color(0xFF141B27)
private val AccentBlue    = Color(0xFF1A6BFF)
private val AccentGold    = Color(0xFFFFB300)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF8A95A8)

// ── Data models ────────────────────────────────────────────────────────────────
data class KnownForMovie(
    val title: String,
    val year: String,
    val rating: Float,
    val emoji: String,
    val gradientColors: List<Color>
)

data class PostItem(
    val source: String,
    val sourceInitial: String,
    val sourceColor: Color,
    val timeAgo: String,
    val headline: String,
    val imageEmoji: String,
    val imageBg: List<Color>
)

// ── Sample data ────────────────────────────────────────────────────────────────
private val knownForMovies = listOf(
    KnownForMovie("Shadow Protocol", "2026", 9.2f, "🌑", listOf(Color(0xFF0A0A0A), Color(0xFF1A1A1A))),
    KnownForMovie("The Last Stand",  "2025", 8.8f, "🎭", listOf(Color(0xFF3A1C0A), Color(0xFF1A2744))),
    KnownForMovie("Echoes of Time",  "2024", 8.5f, "🌌", listOf(Color(0xFF0D1B4B), Color(0xFF2D3561))),
    KnownForMovie("The Reckoning",   "2023", 8.1f, "🔥", listOf(Color(0xFF4A1010), Color(0xFF2A0808))),
)

private val posts = listOf(
    PostItem(
        source        = "Entertainment Weekly",
        sourceInitial = "E",
        sourceColor   = Color(0xFF6A1B9A),
        timeAgo       = "2 hours ago",
        headline      = "Marcus Chen talks about his intense preparation for Shadow Protocol",
        imageEmoji    = "🎬",
        imageBg       = listOf(Color(0xFF1A1030), Color(0xFF2D1060))
    ),
    PostItem(
        source        = "The Hollywood Reporter",
        sourceInitial = "H",
        sourceColor   = Color(0xFF1565C0),
        timeAgo       = "1 day ago",
        headline      = "Marcus Chen nominated for Best Actor at the Cannes Film Festival",
        imageEmoji    = "🏆",
        imageBg       = listOf(Color(0xFF3A2800), Color(0xFF1A1200))
    ),
)

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun ActorInfoScreen(onNavigateBack: () -> Unit = {}) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {

        // 1. Hero photo
        item { ProfileHero(onNavigateBack = onNavigateBack) }

        // 2. Name + chips
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text(
                    text = "Marcus Chen",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip("Born 1985")
                    InfoChip("Los Angeles, CA")
                }
                Spacer(modifier = Modifier.height(8.dp))
                InfoChip("Actor, Producer")
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // 3. About
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                SectionLabel("About")
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Award-winning actor known for his versatile performances across action, drama, and thriller genres. With over 15 years in the industry, Marcus has captivated audiences worldwide with his intense method acting and dedication to his craft. He has received multiple accolades including Best Actor nominations at major film festivals.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // 4. Best Known For
        item {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                SectionLabel("Best Known For", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    knownForMovies.forEach { MovieThumbnail(it) }
                }
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // 5. Posts header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                SectionLabel("Posts About Marcus")
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 6. Post cards
        items(posts) { post ->
            PostCard(post = post)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ── Profile Hero ───────────────────────────────────────────────────────────────
@Composable
private fun ProfileHero(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {

        // Photo area (swap with AsyncImage for real photo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A0A0A), Color(0xFF1A1A2E), Color(0xFF0A0A0A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder portrait circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2A2A3E), Color(0xFF0A0A14))
                        )
                    )
                    .border(2.dp, CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎭", fontSize = 72.sp)
            }
        }

        // Bottom scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, BgDark)))
        )

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
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(20.dp))
        }

        // Rating badge — top right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 52.dp, end = 14.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(15.dp))
            Text(text = "9.1", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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

// ── Section label ──────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text = text, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = modifier)
}

// ── Movie thumbnail ────────────────────────────────────────────────────────────
@Composable
private fun MovieThumbnail(movie: KnownForMovie) {
    Column(modifier = Modifier.width(120.dp)) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(movie.gradientColors))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            // Content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = movie.emoji, fontSize = 36.sp)
            }

            // Rating badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(11.dp))
                Text(text = movie.rating.toString(), color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = movie.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(text = movie.year,  color = TextSecondary, fontSize = 11.sp)
    }
}

// ── Post card ──────────────────────────────────────────────────────────────────
@Composable
private fun PostCard(post: PostItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        // Source row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(post.sourceColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = post.sourceInitial, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(text = post.source,  color = TextPrimary,   fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = post.timeAgo, color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Headline
        Text(
            text = post.headline,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Post image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(post.imageBg))
                .border(1.dp, CardBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = post.imageEmoji, fontSize = 52.sp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun PersonProfileScreenPreview() {
    ActorInfoScreen()
}