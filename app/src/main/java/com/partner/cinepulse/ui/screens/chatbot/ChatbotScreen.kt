package com.partner.cinepulse.ui.screens.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.BotBubble
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.EmeraldGreen
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary
import kotlinx.coroutines.launch

// ── Colour tokens ──────────────────────────────────────────────────────────────
//private val BgDark         = Color(0xFF080C14)
//private val CardDark       = Color(0xFF0F1623)
//private val CardBorder     = Color(0xFF1C2333)
//private val AccentBlue     = Color(0xFF1A6BFF)
//private val BotBubble      = Color(0xFF0F1A2E)
//private val UserBubble     = Color(0xFF1A6BFF)
//private val TextPrimary    = Color(0xFFFFFFFF)
//private val TextSecondary  = Color(0xFF8A95A8)
//private val InputBg        = Color(0xFF0F1623)
//private val OnlineDot      = Color(0xFF00C853)

// ── Data model ─────────────────────────────────────────────────────────────────
data class ChatMessage(
    val id: String,
    val text: String,
    val isBot: Boolean,
    val time: String,
    val movieCard: MovieSuggestion? = null
)

data class MovieSuggestion(
    val title: String,
    val year: String,
    val duration: String,
    val emoji: String,
    val gradientColors: List<Color>
)

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun ChatbotScreen(onNavigateBack: () -> Boolean) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                text = "Hello! I'm your AI Movie Concierge. I can help you discover films, get recommendations, and discuss cinema. What are you in the mood for today?",
                isBot = true,
                time = "10:30 AM"
            ),
            ChatMessage(
                id = "2",
                text = "Looking for a mind-bending thriller similar to Inception",
                isBot = false,
                time = "10:32 AM"
            ),
            ChatMessage(
                id = "3",
                text = "Great choice! Based on your love for Inception, I have some perfect recommendations for you. Let me show you films with similar complex narratives and stunning visuals.",
                isBot = true,
                time = "10:32 AM",
                movieCard = MovieSuggestion(
                    title = "ARRIVAL",
                    year = "2016",
                    duration = "116 min",
                    emoji = "👽",
                    gradientColors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                )
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        ChatHeader()

        // ── Messages ──────────────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }

        // ── Input bar ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgDark)
        ) {
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Text field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(28.dp))
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = TextPrimary,
                                fontSize = 14.sp
                            ),
                            decorationBox = { inner ->
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "Ask me for a personalized movie su...",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                                inner()
                            }
                        )
                        // Mic icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CardDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎙", fontSize = 16.sp)
                        }
                    }
                }

                // Send button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A6BFF), Color(0xFF0D47A1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                messages.add(
                                    ChatMessage(
                                        id = System.currentTimeMillis().toString(),
                                        text = inputText,
                                        isBot = false,
                                        time = "Now"
                                    )
                                )
                                inputText = ""
                                coroutineScope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Powered by footer
            Text(
                text = "Powered by CinePulse AI • Always improving",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 80.dp)
            )
        }
    }
}

// ── Chat Header ────────────────────────────────────────────────────────────────
@Composable
private fun ChatHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bot avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1565C0), Color(0xFF4A148C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 22.sp)
            }

            // Title + status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Movie Concierge",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen )
                    )
                    Text(text = "Online • Instant responses", color = EmeraldGreen , fontSize = 12.sp)
                }
            }

            // Sparkle icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardDark)
                    .border(1.dp, CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✨", fontSize = 18.sp)
            }
        }

        HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
    }
}

// ── Message Bubble ─────────────────────────────────────────────────────────────
@Composable
private fun MessageBubble(message: ChatMessage) {
    if (message.isBot) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            // Bot avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1565C0), Color(0xFF4A148C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.widthIn(max = 260.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .background(BotBubble)
                        .border(1.dp, CardBorder, RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = message.text,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }

                // Movie card if present
                message.movieCard?.let { movie ->
                    Spacer(modifier = Modifier.height(8.dp))
                    MovieCard(movie = movie)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message.time, color = TextSecondary, fontSize = 11.sp)
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.widthIn(max = 240.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp))
                        .background(AccentBlue)
                        .padding(12.dp)
                ) {
                    Text(
                        text = message.text,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message.time, color = TextSecondary, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4158D0), Color(0xFFC850C0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 16.sp)
            }
        }
    }
}

// ── Movie Suggestion Card ──────────────────────────────────────────────────────
@Composable
private fun MovieCard(movie: MovieSuggestion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Poster thumbnail
        Box(
            modifier = Modifier
                .size(width = 70.dp, height = 80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(movie.gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = movie.emoji, fontSize = 28.sp)
        }

        Column {
            Text(
                text = movie.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${movie.year} • ${movie.duration}",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

// Need this import for BasicTextField
@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun ChatbotScreenPreview() {
    ChatbotScreen(onNavigateBack = { false })
}