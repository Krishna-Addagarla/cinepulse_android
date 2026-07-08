package com.partner.cinepulse.ui.screens.createpost

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partner.cinepulse.data.remote.models.searchItem
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.screens.home.HomeScreenViewModel
import com.partner.cinepulse.ui.screens.home.TagDisplayChip
import com.partner.cinepulse.ui.theme.*

private val mockArtistSuggestions = listOf(
    searchItem(id = 1, name = "Christopher Nolan", type = "artist", photo_url = "", rating = 4.8, subtitle = "Director", match_score = 1.0),
    searchItem(id = 2, name = "Cillian Murphy", type = "artist", photo_url = "", rating = 4.7, subtitle = "Actor", match_score = 1.0),
    searchItem(id = 3, name = "Margot Robbie", type = "artist", photo_url = "", rating = 4.6, subtitle = "Actor", match_score = 1.0),
    searchItem(id = 4, name = "Leonardo DiCaprio", type = "artist", photo_url = "", rating = 4.9, subtitle = "Actor", match_score = 1.0),
    searchItem(id = 5, name = "Robert Downey Jr.", type = "artist", photo_url = "", rating = 4.8, subtitle = "Actor", match_score = 1.0)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val tagSuggestions by viewModel.tagSuggestions.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            viewModel.searchTagSuggestions(tagQuery)
        }
    }

    LaunchedEffect(uiState.postCreateSuccess) {
        if (uiState.postCreateSuccess) {
            viewModel.resetPostCreateState()
            onNavigateBack()
        }
    }

    val displaySuggestions = remember(tagQuery, tagSuggestions) {
        if (isTagging) {
            if (tagQuery.isNotEmpty()) {
                val locals = mockArtistSuggestions.filter { it.name.contains(tagQuery, ignoreCase = true) }
                (tagSuggestions + locals).distinctBy { it.id to it.type }
            } else {
                mockArtistSuggestions
            }
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Create Post",
                showBackButton = true,
                showNotificationIcon = false,
                showProfileIcon = false,
                onBackClick = onNavigateBack
            )
        },
        containerColor = BgDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Share your cinematic thoughts! Mention at least one artist, movie, or TV show using @ to tag them.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("What's on your mind? Type @ to tag movies, artists, or shows...", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark
                ),
                maxLines = 10
            )

            // Autocomplete Tag Suggestions Box
            if (isTagging && displaySuggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(displaySuggestions) { suggestion ->
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
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (suggestion.type) {
                                        "artist" -> "👤  ${suggestion.name}"
                                        "movie" -> "🎬  ${suggestion.name}"
                                        "tv_show" -> "📺  ${suggestion.name}"
                                        else -> "👤  ${suggestion.name}"
                                    },
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Tagged Chips Area
            val totalTags = taggedArtists.size + taggedMovies.size + taggedTvshows.size
            if (totalTags > 0) {
                Text(
                    text = "Tagged entities ($totalTags):",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
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

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = AccentRed,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            val hasMinTag = taggedArtists.isNotEmpty() || taggedMovies.isNotEmpty() || taggedTvshows.isNotEmpty()
            val canPost = text.trim().isNotEmpty() && hasMinTag && !uiState.isPostCreating

            Button(
                onClick = {
                    viewModel.createPost(
                        content = text,
                        taggedArtistIds = taggedArtists.map { it.id },
                        taggedMovieIds = taggedMovies.map { it.id },
                        taggedTvshowIds = taggedTvshows.map { it.id }
                    ) {
                        viewModel.resetPostCreateState()
                        onNavigateBack()
                    }
                },
                enabled = canPost,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isPostCreating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Publish Post", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
