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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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

@Composable
fun SearchScreen(
    onNavigateBack: () -> Boolean,
    onNavigateToMovie: (id: Int) -> Unit,
    onNavigateToActor: (id: Int) -> Unit,
    onNavigateToFanclub: (id: Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uistate.collectAsStateWithLifecycle()
    val searchResponse by viewModel.searchResult.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    var isSearchFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isShowingResults = query.isNotBlank()

    // Scroll Position Restoration
    val scrollIndexState by viewModel.scrollIndex.collectAsStateWithLifecycle()
    val scrollOffsetState by viewModel.scrollOffset.collectAsStateWithLifecycle()

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = scrollIndexState,
        initialFirstVisibleItemScrollOffset = scrollOffsetState
    )

    // Save scroll state when it changes
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        viewModel.saveScrollPosition(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
    }

    // Debounced Search Call
    LaunchedEffect(query) {
        if (query.isNotBlank() && query != searchResponse?.query) {
            kotlinx.coroutines.delay(400L)
            viewModel.searchContent(query)
        }
    }

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
                    onValueChange = { viewModel.setQuery(it) },
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
                        onSearch = {
                            if (query.isNotBlank()) {
                                viewModel.searchContent(query)
                            }
                            focusManager.clearFocus()
                        }
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
                                viewModel.setQuery("")
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
                    searchResponse = searchResponse,
                    listState = listState,
                    onNavigateToMovie = onNavigateToMovie,
                    onNavigateToActor = onNavigateToActor,
                    onNavigateToFanclub = onNavigateToFanclub,
                    onRetry = { viewModel.searchContent(query) }
                )
            }

            AnimatedVisibility(
                visible = !isShowingResults,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DiscoveryPanel(
                    recentSearches = uiState.recentSearches,
                    trendingSearches = uiState.trendingSearches,
                    suggestions = uiState.suggestions,
                    isTrendingLoading = uiState.isTrendingLoading,
                    isSuggestionsLoading = uiState.isSuggestionsLoading,
                    trendingErrorMessage = uiState.trendingErrorMessage,
                    suggestionsErrorMessage = uiState.suggestionsErrorMessage,
                    onChipClick = { tag ->
                        viewModel.setQuery(tag)
                        viewModel.searchContent(tag)
                        focusManager.clearFocus()
                    },
                    onDeleteChip = { tag ->
                        viewModel.removeRecentSearch(tag)
                    },
                    onClearAllRecent = {
                        viewModel.clearAllRecentSearches()
                    },
                    onRetryTrending = {
                        viewModel.refreshTrendingAndSuggestions()
                    },
                    onRetrySuggestions = {
                        viewModel.refreshTrendingAndSuggestions()
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
    searchResponse: searchResponse?,
    listState: LazyListState,
    onNavigateToMovie: (id: Int) -> Unit,
    onNavigateToActor: (id: Int) -> Unit,
    onNavigateToFanclub: (id: Int) -> Unit,
    onRetry: () -> Unit
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                        ) {
                            Text("Retry", color = BgDark)
                        }
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
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResponse.results, key = { "${it.id}_${it.type}" }) { item ->
                        SearchResultCard(
                            item = item,
                            onClick = { selectedItem ->
                                when (selectedItem.type.lowercase()) {
                                    "movie" -> onNavigateToMovie(selectedItem.id)
                                    "tv_show" -> onNavigateToMovie(selectedItem.id) // Map TV Show details
                                    "artist" -> onNavigateToActor(selectedItem.id)
                                    "fanclub" -> onNavigateToFanclub(selectedItem.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(item: searchItem, onClick: (searchItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick(item) }
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
            if (!item.photo_url.isNullOrBlank()) {
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
                Text(
                    text = item.subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
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
    trendingSearches: List<searchItem>,
    suggestions: List<searchItem>,
    isTrendingLoading: Boolean,
    isSuggestionsLoading: Boolean,
    trendingErrorMessage: String?,
    suggestionsErrorMessage: String?,
    onChipClick: (String) -> Unit,
    onDeleteChip: (String) -> Unit,
    onClearAllRecent: () -> Unit,
    onRetryTrending: () -> Unit,
    onRetrySuggestions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        if (recentSearches.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(icon = "🕐", title = "Recent Searches")
                Text(
                    text = "Clear All",
                    color = AccentGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onClearAllRecent() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSearches) { tag ->
                    RecentChip(text = tag, onClick = { onChipClick(tag) }, onDeleteClick = { onDeleteChip(tag) })
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        SectionHeader(icon = "📈", title = "Trending Searches")
        Spacer(modifier = Modifier.height(12.dp))
        when {
            isTrendingLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentGreen, strokeWidth = 2.dp)
                }
            }
            trendingErrorMessage != null && trendingSearches.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Trending unavailable", color = TextSecondary, fontSize = 13.sp)
                        Button(onClick = onRetryTrending, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) {
                            Text("Retry", color = BgDark, fontSize = 12.sp)
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trendingSearches.take(5).forEachIndexed { index, item ->
                        TrendingCard(rank = index + 1, item = item, onClick = { onChipClick(item.name) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionHeader(icon = "✨", title = "Suggested for You")
        Spacer(modifier = Modifier.height(12.dp))
        when {
            isSuggestionsLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentGreen, strokeWidth = 2.dp)
                }
            }
            suggestionsErrorMessage != null && suggestions.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Suggestions unavailable", color = TextSecondary, fontSize = 13.sp)
                        Button(onClick = onRetrySuggestions, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) {
                            Text("Retry", color = BgDark, fontSize = 12.sp)
                        }
                    }
                }
            }
            else -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(suggestions) { item ->
                        SuggestedCard(item = item, onClick = { onChipClick(item.name) })
                    }
                }
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
private fun RecentChip(text: String, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSlate)
            .border(1.dp, ChipBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Delete",
            tint = TextSecondary,
            modifier = Modifier
                .size(14.dp)
                .clickable { onDeleteClick() }
        )
    }
}

@Composable
private fun TrendingCard(rank: Int, item: searchItem, onClick: () -> Unit = {}) {
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
            text = "$rank",
            color = TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = item.subtitle ?: "Trending content", color = TextSecondary, fontSize = 12.sp)
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
private fun SuggestedCard(item: searchItem, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        if (!item.photo_url.isNullOrBlank()) {
            AsyncImage(
                model = item.photo_url,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(DarkSlate),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎬", fontSize = 36.sp)
            }
        }
        
        if (item.rating > 0.0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(text = "⭐ ${"%.1f".format(item.rating)}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                .padding(10.dp)
        ) {
            Text(text = item.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}