package com.partner.cinepulse.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.Credit
import com.partner.cinepulse.data.remote.models.Role
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.ui.screens.writereview.WriteReviewViewModel
import com.partner.cinepulse.utils.Resource

// ── Colour tokens ──────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF080C14)
private val CardDark      = Color(0xFF0F1623)
private val CardBorder    = Color(0xFF1C2333)
private val AccentBlue    = Color(0xFF1A6BFF)
private val AccentGold    = Color(0xFFFFB300)
private val AccentRed     = Color(0xFFE53935)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF8A95A8)

// ── Role classification ────────────────────────────────────────────────────────
private val castRoleNames = setOf("Actor", "Actress")
private fun List<Credit>.castMembers() = filter { it.role.name in castRoleNames }
private fun List<Credit>.crewMembers() = filter { it.role.name !in castRoleNames }

// ── Fake data for Preview ──────────────────────────────────────────────────────
private val previewMovieState: Resource<movieResponse> = Resource.Success(
    movieResponse(
        id              = 1,
        title           = "Interstellar",
        plot            = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
        photo_url       = "",
        release_date    = "2014-11-07",
        release_year    = 2014,
        runtime_minutes = 169,
        overall_rating  = 4.8,
        total_ratings   = 12000,
        genres          = listOf("Sci-Fi", "Drama"),
        awards          = emptyList(),
        credits         = listOf(
            Credit(id = 1, name = "Matthew McConaughey", photo_url = "", role = Role("Actor", 1),           character_name = "Cooper", rating = 9.2, total_ratings = 800),
            Credit(id = 2, name = "Anne Hathaway",       photo_url = "", role = Role("Actress", 2),         character_name = "Brand",  rating = 8.8, total_ratings = 600),
            Credit(id = 3, name = "Christopher Nolan",   photo_url = "", role = Role("Director", 3),        character_name = "",       rating = 9.9, total_ratings = 1000),
            Credit(id = 4, name = "Hoyte van Hoytema",   photo_url = "", role = Role("Cinematographer", 4), character_name = "",       rating = 9.0, total_ratings = 300),
        )
    )
)

// ── Outer screen (ViewModel-aware) ─────────────────────────────────────────────
@Composable
fun WriteReviewScreen(
    onNavigateBack  : () -> Unit = {},
    onReviewPosted  : () -> Unit = {},   // called on success → navigate away
    movieId         : Int?,
    viewModel       : WriteReviewViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        movieId?.let { viewModel.loadMovie(it) }
    }

    val movieState by viewModel.movieState.collectAsState()
    val uiState    by viewModel.uiState.collectAsState()

    // Navigate away as soon as the review is successfully posted
    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) onReviewPosted()
    }

    WriteReviewContent(
        onNavigateBack = onNavigateBack,
        movieState     = movieState,
        isSubmitting   = uiState.isLoading,
        errorMessage   = uiState.errorMessage,
        onSubmit       = { reviewText, altPlotText, overallRating, castRatings, crewRatings ->
            movieId?.let {
                viewModel.submitReview(
                    movieId       = it,
                    overallRating = overallRating,
                    reviewText    = if (altPlotText.isBlank()) reviewText
                    else "$reviewText\n\n[Alt plot]: $altPlotText",
                    castRatings   = castRatings,
                    crewRatings   = crewRatings
                )
            }
        }
    )
}

// ── Inner content composable (preview-safe, no ViewModel) ──────────────────────
@Composable
fun WriteReviewContent(
    onNavigateBack : () -> Unit = {},
    movieState     : Resource<movieResponse>,
    isSubmitting   : Boolean = false,
    errorMessage   : String? = null,
    onSubmit       : (
        reviewText    : String,
        altPlotText   : String,
        overallRating : Float,
        castRatings   : Map<Int, Float>,
        crewRatings   : Map<Int, Float>
    ) -> Unit = { _, _, _, _, _ -> }
) {
    val credits     = if (movieState is Resource.Success) movieState.data.credits else emptyList()
    val castCredits = remember(credits) { credits.castMembers() }
    val crewCredits = remember(credits) { credits.crewMembers() }

    val castRatings   = remember { mutableStateMapOf<Int, Float>() }
    val crewRatings   = remember { mutableStateMapOf<Int, Float>() }
    var reviewText    by remember { mutableStateOf("") }
    var altPlotText   by remember { mutableStateOf("") }
    var overallRating by remember { mutableFloatStateOf(0f) }

    // Basic validation — overall rating and at least some review text required
    val canSubmit = overallRating > 0f && reviewText.isNotBlank() && !isSubmitting

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier       = Modifier.fillMaxSize().background(BgDark),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── Top bar ───────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment     = Alignment.CenterVertically,
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
                        Icon(
                            imageVector        = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint               = TextPrimary,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text       = "Write a Review",
                        color      = TextPrimary,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // ── API error banner ──────────────────────────────────────────────
            if (errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AccentRed.copy(alpha = 0.15f))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text     = "⚠ $errorMessage",
                            color    = AccentRed,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ── Movie Plot synopsis ───────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    SectionLabel("Plot")
                    Spacer(modifier = Modifier.height(10.dp))
                    when (movieState) {
                        is Resource.Loading -> CircularProgressIndicator(color = AccentBlue)
                        is Resource.Success -> Text(
                            text       = movieState.data.plot,
                            color      = TextSecondary,
                            fontSize   = 13.sp,
                            lineHeight = 21.sp
                        )
                        is Resource.Error   -> Text(
                            text     = movieState.message,
                            color    = AccentRed,
                            fontSize = 13.sp
                        )
                    }
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // ── Rate the Cast ─────────────────────────────────────────────────
            if (castCredits.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        SectionLabel("Rate the Cast", modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(castCredits.size) { idx ->
                                val credit = castCredits[idx]
                                CreditRateCard(
                                    credit         = credit,
                                    rating         = castRatings[credit.id] ?: 0f,
                                    onRatingChange = { castRatings[credit.id] = it }
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                }
            }

            // ── Rate the Crew ─────────────────────────────────────────────────
            if (crewCredits.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        SectionLabel("Rate the Crew", modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(crewCredits.size) { idx ->
                                val credit = crewCredits[idx]
                                CreditRateCard(
                                    credit         = credit,
                                    rating         = crewRatings[credit.id] ?: 0f,
                                    onRatingChange = { crewRatings[credit.id] = it }
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                }
            }

            // ── Write Your Review ─────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    SectionLabel("Write Your Review")
                    Spacer(modifier = Modifier.height(12.dp))
                    ReviewTextField(
                        value         = reviewText,
                        onValueChange = { if (it.length <= 1000) reviewText = it },
                        placeholder   = "Share your thoughts about the movie...",
                        maxChars      = 1000,
                        minHeight     = 130
                    )
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // ── Alternative Plot ──────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    SectionLabel("Alternative Plot")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text     = "How would you have written it differently?",
                        color    = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ReviewTextField(
                        value         = altPlotText,
                        onValueChange = { if (it.length <= 500) altPlotText = it },
                        placeholder   = "Suggest an alternative plot or ending...",
                        maxChars      = 500,
                        minHeight     = 110
                    )
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // ── Overall Rating ────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    SectionLabel("Overall Rating")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text     = "How would you rate this movie overall?",
                        color    = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        repeat(5) { i ->
                            val filled = i < overallRating
                            Icon(
                                imageVector        = if (filled) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star ${i + 1}",
                                tint               = if (filled) AccentGold else TextSecondary,
                                modifier           = Modifier
                                    .size(40.dp)
                                    .clickable { overallRating = (i + 1).toFloat() }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val ratingLabel = when (overallRating.toInt()) {
                        0    -> "Tap a star to rate"
                        1    -> "⭐ Poor"
                        2    -> "⭐⭐ Fair"
                        3    -> "⭐⭐⭐ Good"
                        4    -> "⭐⭐⭐⭐ Great"
                        5    -> "⭐⭐⭐⭐⭐ Masterpiece!"
                        else -> ""
                    }
                    Text(
                        text       = ratingLabel,
                        color      = if (overallRating > 0) AccentGold else TextSecondary,
                        fontSize   = 14.sp,
                        fontWeight = if (overallRating > 0) FontWeight.Bold else FontWeight.Normal,
                        modifier   = Modifier.fillMaxWidth(),
                        textAlign  = TextAlign.Center
                    )

                    if (overallRating > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text      = "${overallRating.toInt()}.0 / 5.0",
                            color     = TextSecondary,
                            fontSize  = 12.sp,
                            modifier  = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            }

            // ── Submit button ─────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (canSubmit) AccentBlue else AccentBlue.copy(alpha = 0.4f))
                        .clickable(enabled = canSubmit) {
                            onSubmit(
                                reviewText,
                                altPlotText,
                                overallRating,
                                castRatings.toMap(),
                                crewRatings.toMap()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color     = TextPrimary,
                            modifier  = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text       = "Submit Review",
                            color      = TextPrimary,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Validation hint
                if (overallRating == 0f || reviewText.isBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text      = "Please add a star rating and write your review to submit",
                        color     = TextSecondary,
                        fontSize  = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Full-screen loading overlay while submitting
        if (isSubmitting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(enabled = false) {},  // block touches
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AccentBlue)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Submitting review...", color = TextPrimary, fontSize = 14.sp)
                }
            }
        }
    }
}

// ── Credit Rate Card ───────────────────────────────────────────────────────────
@Composable
private fun CreditRateCard(
    credit: Credit,
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    Column(
        modifier            = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model              = credit.photo_url,
                contentDescription = credit.name,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier              = Modifier.fillMaxWidth()
        ) {
            repeat(5) { i ->
                Icon(
                    imageVector        = if (i < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Star ${i + 1}",
                    tint               = if (i < rating) AccentGold else TextSecondary,
                    modifier           = Modifier
                        .size(20.dp)
                        .clickable { onRatingChange((i + 1).toFloat()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text       = credit.name,
            color      = TextPrimary,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines   = 1
        )
        // Cast → character name; Crew → role title
        val subtitle = if (credit.character_name.isNotBlank()) credit.character_name else credit.role.name
        Text(text = subtitle, color = TextSecondary, fontSize = 10.sp, maxLines = 1)
        Text(
            text       = if (rating > 0) "${rating.toInt()}/5" else "—",
            color      = if (rating > 0) AccentGold else TextSecondary,
            fontSize   = 11.sp,
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
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            textStyle     = TextStyle(color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp),
            cursorBrush   = SolidColor(AccentBlue)
        )
        Text(
            text     = "${value.length}/$maxChars",
            color    = TextSecondary,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
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

// ── Previews ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun WriteReviewContentPreview() {
    WriteReviewContent(movieState = previewMovieState)
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14, name = "Loading state")
@Composable
fun WriteReviewLoadingPreview() {
    WriteReviewContent(movieState = Resource.Loading())
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14, name = "Error state")
@Composable
fun WriteReviewErrorPreview() {
    WriteReviewContent(
        movieState   = previewMovieState,
        errorMessage = "Failed to submit review. Please try again."
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14, name = "Submitting state")
@Composable
fun WriteReviewSubmittingPreview() {
    WriteReviewContent(
        movieState   = previewMovieState,
        isSubmitting = true
    )
}