package com.partner.cinepulse.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.partner.cinepulse.ui.theme.AccentGreen
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.DarkSlate
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary
import com.partner.cinepulse.ui.theme.ChipBorder


// ── Colour tokens ──────────────────────────────────────────────────────────────
//private val BgDark        = Color(0xFF080C14)
//private val CardDark      = Color(0xFF0F1623)
//private val CardBorder    = Color(0xFF1C2333)
//private val AccentRed     = Color(0xFFE50914)
//private val AccentGreen   = Color(0xFF1DB954)
//private val TextPrimary   = Color(0xFFFFFFFF)
//private val TextSecondary = Color(0xFF8A95A8)
//private val SearchBg      = Color(0xFF0F1623)
//private val ChipBg        = Color(0xFF141B27)
//private val ChipBorder    = Color(0xFF1E2A3A)

// ── Data models ────────────────────────────────────────────────────────────────
data class TrendingItem(val rank: Int, val title: String, val searches: String)
data class SuggestedItem(val title: String, val rating: Float, val color1: Color, val color2: Color)

@Composable
fun SearchScreen(onNavigateBack: () -> Boolean) {
    var query by remember { mutableStateOf("") }

    val recentSearches = listOf("Oppenheimer", "Denis Villeneuve", "Sci-Fi Thriller", "Christopher Nolan")

    val trendingItems = listOf(
        TrendingItem(1, "Dune Part Two",              "12.5K searches"),
        TrendingItem(2, "Killers of the Flower Moon", "8.3K searches"),
        TrendingItem(3, "Poor Things",                "6.7K searches"),
        TrendingItem(4, "The Zone of Interest",       "5.2K searches"),
    )

    val suggestedItems = listOf(
        SuggestedItem("Oppenheimer",  4.6f, Color(0xFFB05C1A), Color(0xFF6B3A10)),
        SuggestedItem("Toy Story",    4.4f, Color(0xFF2E7D32), Color(0xFF1B5E20)),
        SuggestedItem("Inception",    4.8f, Color(0xFF1565C0), Color(0xFF0D47A1)),
        SuggestedItem("The Godfather",4.9f, Color(0xFF4A148C), Color(0xFF311B92)),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        TopBar(
            title = "Search",
            showSearchIcon = false,
            showNotificationIcon = false,
            showProfileIcon = false,
            showBackButton = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // ── Search bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (query.isEmpty()) "Search movies, directors, actors..." else query,
                    color = if (query.isEmpty()) TextSecondary else TextPrimary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Recent Searches ───────────────────────────────────────────────
            SectionHeader(icon = "🕐", title = "Recent Searches")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSearches) { tag ->
                    RecentChip(text = tag)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Trending Searches ─────────────────────────────────────────────
            SectionHeader(icon = "📈", title = "Trending Searches")
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trendingItems.forEach { item ->
                    TrendingCard(item = item)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Suggested for You ─────────────────────────────────────────────
            SectionHeader(icon = "", title = "Suggested for You")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(suggestedItems) { item ->
                    SuggestedCard(item = item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: String, title: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecentChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSlate)
            .border(1.dp, ChipBorder, RoundedCornerShape(20.dp))
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TrendingCard(item: TrendingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text = "${item.rank}",
            color = TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Title + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.searches,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Trending arrow icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AccentGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "↗", color = AccentGreen, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SuggestedCard(item: SuggestedItem) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(item.color1, item.color2)
                )
            )
            .clickable { }
    ) {
        // Rating badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(
                text = "⭐ ${item.rating}",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Title at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .padding(10.dp)
        ) {
            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun SearchScreenPreview() {
    SearchScreen(onNavigateBack = { false })
}