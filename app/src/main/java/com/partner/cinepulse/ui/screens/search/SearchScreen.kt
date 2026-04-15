package com.partner.cinepulse.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.searchItem
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.*

data class TrendingItem(val rank: Int, val title: String, val searches: String)
data class SuggestedItem(val title: String, val rating: Float, val color1: Color, val color2: Color)

@Composable
fun SearchScreen(
    onNavigateBack: () -> Boolean,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uistate.collectAsStateWithLifecycle()
    val searchResponse by viewModel.searchResult.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            kotlinx.coroutines.delay(400L)
            viewModel.searchContent(query)
        }
    }

    val isShowingResults = query.isNotBlank()

    val recentSearches = listOf("Oppenheimer", "Denis Villeneuve", "Sci-Fi Thriller", "Christopher Nolan")

    val trendingItems = listOf(
        TrendingItem(1, "Dune Part Two", "12.5K searches"),
        TrendingItem(2, "Killers of the Flower Moon", "8.3K searches"),
        TrendingItem(3, "Poor Things", "6.7K searches"),
        TrendingItem(4, "The Zone of Interest", "5.2K searches"),
    )

    val suggestedItems = listOf(
        SuggestedItem("Oppenheimer", 4.6f, Color(0xFFB05C1A), Color(0xFF6B3A10)),
        SuggestedItem("Toy Story", 4.4f, Color(0xFF2E7D32), Color(0xFF1B5E20)),
        SuggestedItem("Inception", 4.8f, Color(0xFF1565C0), Color(0xFF0D47A1)),
        SuggestedItem("The Godfather", 4.9f, Color(0xFF4A148C), Color(0xFF311B92)),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopBar(
            title = "Search",
            showSearchIcon = false,
            showNotificationIcon = false,
            showProfileIcon = false,
            showBackButton = false
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .border(
                        width = 1.dp,
                        color = if (isSearchFocused) AccentGreen.copy(alpha = 0.6f) else CardBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isSearchFocused) AccentGreen else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 10.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { isSearchFocused = it.isFocused },
                    textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(AccentGreen),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search movies, directors, actors...",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                AnimatedVisibility(visible = query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                query = ""
                                focusRequester.requestFocus()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isShowingResults,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SearchResultsPanel(
                    query = query,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    searchResponse = searchResponse
                )
            }

            AnimatedVisibility(
                visible = !isShowingResults,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DiscoveryPanel(
                    recentSearches = recentSearches,
                    trendingItems = trendingItems,
                    suggestedItems = suggestedItems,
                    onChipClick = { tag ->
                        query = tag
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchResultsPanel(
    query: String,
    isLoading: Boolean,
    errorMessage: String?,
    searchResponse: searchResponse?
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (searchResponse != null && !isLoading) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (searchResponse.total == 0) "No results for"
                    else "${searchResponse.total} results for",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "\"${searchResponse.query}\"",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentGreen,
                        strokeWidth = 2.dp
                    )
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚠️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Something went wrong",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = errorMessage,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            searchResponse != null && searchResponse.results.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎬", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matches found",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try a different title, director, or genre",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            searchResponse != null -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResponse.results, key = { it.id }) { item ->
                        SearchResultCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(item: searchItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardBorder),
            contentAlignment = Alignment.Center
        ) {
            if (item.photo_url.isNotBlank()) {
                AsyncImage(
                    model = item.photo_url,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = "🎬", fontSize = 20.sp)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (!item.subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))  // Maintain consistent spacing
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CardBorder)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.type.uppercase(),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (item.rating > 0.0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentGreen.copy(alpha = 0.15f))
                            .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⭐ ${"%.1f".format(item.rating)}",
                            color = AccentGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(text = "›", color = TextSecondary, fontSize = 22.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun DiscoveryPanel(
    recentSearches: List<String>,
    trendingItems: List<TrendingItem>,
    suggestedItems: List<SuggestedItem>,
    onChipClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        SectionHeader(icon = "🕐", title = "Recent Searches")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recentSearches) { tag ->
                RecentChip(text = tag, onClick = { onChipClick(tag) })
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionHeader(icon = "📈", title = "Trending Searches")
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            trendingItems.forEach { item ->
                TrendingCard(item = item, onClick = { onChipClick(item.title) })
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionHeader(icon = "✨", title = "Suggested for You")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(suggestedItems) { item ->
                SuggestedCard(item = item, onClick = { onChipClick(item.title) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionHeader(icon: String, title: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(text = title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RecentChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSlate)
            .border(1.dp, ChipBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TrendingCard(item: TrendingItem, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.rank}",
            color = TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = item.searches, color = TextSecondary, fontSize = 12.sp)
        }
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
private fun SuggestedCard(item: SuggestedItem, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(item.color1, item.color2)))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(text = "⭐ ${item.rating}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                .padding(10.dp)
        ) {
            Text(text = item.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun SearchScreenPreview() {
    SearchScreen(onNavigateBack = { false })
}