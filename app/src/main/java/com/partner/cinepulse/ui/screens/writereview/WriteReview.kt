package com.partner.cinepulse.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ──────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF080C14)
private val CardDark      = Color(0xFF0F1623)
private val CardBorder    = Color(0xFF1C2333)
private val AccentBlue    = Color(0xFF1A6BFF)
private val AccentGold    = Color(0xFFFFB300)
private val AccentGreen   = Color(0xFF1DB954)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF8A95A8)
private val SliderTrack   = Color(0xFF1C2333) // kept for reference

// ── Data models ────────────────────────────────────────────────────────────────
data class RateablePerson(
    val name: String,
    val role: String,
    val emoji: String,
    val bgColors: List<Color>
)

// ── Sample data ────────────────────────────────────────────────────────────────
private val castList = listOf(
    RateablePerson("Marcus Chen",   "Lead Actor",       "🎭", listOf(Color(0xFF1B3A1B), Color(0xFF2E5E2E))),
    RateablePerson("Sarah Williams","Lead Actress",     "🌟", listOf(Color(0xFF3A1020), Color(0xFF6B1A30))),
    RateablePerson("Alex Thompson", "Supporting Actor", "🎬", listOf(Color(0xFF1A2040), Color(0xFF2A3060))),
)

private val crewList = listOf(
    RateablePerson("David Rodriguez","Director",       "🎬", listOf(Color(0xFF1A2A1A), Color(0xFF2A402A))),
    RateablePerson("Emily Zhang",    "Cinematographer","📷", listOf(Color(0xFF1A1A3A), Color(0xFF2A2A5A))),
    RateablePerson("James Mitchell", "Screenplay",     "✍️", listOf(Color(0xFF2A2010), Color(0xFF3A3020))),
)

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun WriteReviewScreen(onNavigateBack: () -> Unit = {}, onSubmit: () -> Unit = {}) {

    // Cast ratings
    val castRatings = remember { castList.map { mutableFloatStateOf(0f) } }
    // Crew ratings
    val crewRatings = remember { crewList.map { mutableFloatStateOf(0f) } }
    // Text fields
    var reviewText   by remember { mutableStateOf("") }
    var altPlotText  by remember { mutableStateOf("") }
    // Overall star rating
    var overallRating by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // ── Top bar ───────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardDark)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
                Text(text = "Write a Review", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Movie Plot synopsis ───────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                SectionLabel("Plot")
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "When a former intelligence operative discovers a global conspiracy that threatens to destabilize world governments, she must navigate a dangerous web of deception and betrayal. With time running out and enemies closing in from all sides, she assembles an unlikely team to expose the truth before it's too late.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 21.sp
                )
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Rate the Cast ─────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                SectionLabel("Rate the Cast", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(14.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    castList.forEachIndexed { idx, person ->
                        item {
                            RatePersonCard(
                                person = person,
                                rating = castRatings[idx].floatValue,
                                onRatingChange = { castRatings[idx].floatValue = it }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Rate the Crew ─────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                SectionLabel("Rate the Crew", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(14.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    crewList.forEachIndexed { idx, person ->
                        item {
                            RatePersonCard(
                                person = person,
                                rating = crewRatings[idx].floatValue,
                                onRatingChange = { crewRatings[idx].floatValue = it }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Write Your Review ─────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                SectionLabel("Write Your Review")
                Spacer(modifier = Modifier.height(12.dp))
                ReviewTextField(
                    value       = reviewText,
                    onValueChange = { if (it.length <= 1000) reviewText = it },
                    placeholder = "Share your thoughts about the movie...",
                    maxChars    = 1000,
                    minHeight   = 130
                )
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Alternative Plot ──────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                SectionLabel("Alternative Plot")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "How would you have written it differently?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                ReviewTextField(
                    value       = altPlotText,
                    onValueChange = { if (it.length <= 500) altPlotText = it },
                    placeholder = "Suggest an alternative plot or ending...",
                    maxChars    = 500,
                    minHeight   = 110
                )
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Overall Rating ────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                SectionLabel("Overall Rating")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "How would you rate this movie overall?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Star selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { i ->
                        val filled = i < overallRating
                        Icon(
                            imageVector = if (filled) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Star ${i + 1}",
                            tint = if (filled) AccentGold else TextSecondary,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { overallRating = (i + 1).toFloat() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rating label
                val ratingLabel = when (overallRating.toInt()) {
                    0 -> "Tap a star to rate"
                    1 -> "⭐ Poor"
                    2 -> "⭐⭐ Fair"
                    3 -> "⭐⭐⭐ Good"
                    4 -> "⭐⭐⭐⭐ Great"
                    5 -> "⭐⭐⭐⭐⭐ Masterpiece!"
                    else -> ""
                }
                Text(
                    text = ratingLabel,
                    color = if (overallRating > 0) AccentGold else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (overallRating > 0) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Numeric score display
                if (overallRating > 0) {
                    Text(
                        text = "${overallRating.toInt()}.0 / 5.0",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        }

        // ── Submit button ─────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentBlue)
                    .clickable { onSubmit() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Submit Review",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── Rateable Person Card ───────────────────────────────────────────────────────
@Composable
private fun RatePersonCard(
    person: RateablePerson,
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Photo placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(person.bgColors))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = person.emoji, fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Star rating (1–5)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(5) { i ->
                Icon(
                    imageVector = if (i < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Star ${i + 1}",
                    tint = if (i < rating) AccentGold else TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRatingChange((i + 1).toFloat()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = person.name,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Text(
            text = person.role,
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1
        )
        Text(
            text = if (rating > 0) "${rating.toInt()}/5" else "—",
            color = if (rating > 0) AccentGold else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Review Text Field ──────────────────────────────────────────────────────────
@Composable
private fun ReviewTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxChars: Int,
    minHeight: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = TextSecondary, fontSize = 13.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            textStyle = TextStyle(color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp),
            cursorBrush = SolidColor(AccentBlue)
        )
        // Char counter
        Text(
            text = "${value.length}/$maxChars",
            color = TextSecondary,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

// ── Section label ──────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text = text, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = modifier)
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun WriteReviewScreenPreview() {
    WriteReviewScreen()
}