package com.partner.cinepulse.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partner.cinepulse.ui.theme.*

@Composable
fun OnboardingScreen(
    onOnboardingCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentStep by remember { mutableStateOf(1) }
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    // Preferences state
    val selectedGenres = remember { mutableStateListOf<String>() }
    val selectedLanguages = remember { mutableStateListOf<String>() }
    var selectedRegion by remember { mutableStateOf("US") }

    // Pre-populate selections when profile is loaded
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            if (selectedGenres.isEmpty() && !profile.interests.isNullOrEmpty()) {
                selectedGenres.addAll(profile.interests)
            }
            if (selectedLanguages.isEmpty() && !profile.languages.isNullOrEmpty()) {
                selectedLanguages.addAll(profile.languages)
            }
            if (selectedRegion == "US" && !profile.region.isNullOrEmpty()) {
                selectedRegion = profile.region
            }
        }
    }

    val genres = listOf("Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Thriller", "Romance")
    val languages = listOf("English", "Hindi", "Telugu", "Tamil", "Kannada", "Malayalam", "Marathi", "Gujarati", "Punjabi", "Bengali", "Bhojpuri", "Odia", "Assamese", "Spanish", "French", "Japanese")
    val regions = listOf("US", "IN", "UK", "CA", "FR", "DE", "JP")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Glowing background blobs
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBlue.copy(alpha = 0.12f), Color.Transparent),
                        radius = 1200f,
                        center = androidx.compose.ui.geometry.Offset(0f, 0f)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(SecondBlue.copy(alpha = 0.08f), Color.Transparent),
                        radius = 1350f,
                        center = androidx.compose.ui.geometry.Offset(1000f, 1500f)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CINEPULSE",
                color = AccentBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Immersive Translucent Panel Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xDC0F1623)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Progress Bar Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val step = index + 1
                            val isCurrent = currentStep == step
                            val isCompleted = currentStep > step
                            Box(
                                modifier = Modifier
                                    .animateContentSize()
                                    .height(6.dp)
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when {
                                            isCurrent -> AccentBlue
                                            isCompleted -> EmeraldGreen
                                            else -> Color.White.copy(alpha = 0.05f)
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Heading Content with Transitions
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "heading"
                    ) { step ->
                        Column {
                            Text(
                                text = when (step) {
                                    1 -> "What are your favorite genres?"
                                    2 -> "Which languages do you prefer?"
                                    else -> "Where are you located?"
                                },
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (step) {
                                    1 -> "Select the genres you love to customize your recommendations."
                                    2 -> "We'll prioritize content in your preferred languages."
                                    else -> "This helps customize the trending explore feed for your region."
                                },
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Body Content depending on current step
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        when (currentStep) {
                            1 -> {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    genres.forEach { genre ->
                                        val isSelected = selectedGenres.contains(genre)
                                        PreferenceChip(
                                            text = genre,
                                            isSelected = isSelected,
                                            onClick = {
                                                if (isSelected) selectedGenres.remove(genre)
                                                else selectedGenres.add(genre)
                                            }
                                        )
                                    }
                                }
                            }
                            2 -> {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    languages.forEach { lang ->
                                        val isSelected = selectedLanguages.contains(lang)
                                        PreferenceChip(
                                            text = lang,
                                            isSelected = isSelected,
                                            onClick = {
                                                if (isSelected) selectedLanguages.remove(lang)
                                                else selectedLanguages.add(lang)
                                            }
                                        )
                                    }
                                }
                            }
                            3 -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    regions.forEach { regionCode ->
                                        val isSelected = selectedRegion == regionCode
                                        val regionLabel = when (regionCode) {
                                            "US" -> "🇺🇸 United States"
                                            "IN" -> "🇮🇳 India"
                                            "UK" -> "🇬🇧 United Kingdom"
                                            "CA" -> "🇨🇦 Canada"
                                            "FR" -> "🇫🇷 France"
                                            "DE" -> "🇩🇪 Germany"
                                            "JP" -> "🇯🇵 Japan"
                                            else -> regionCode
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) AccentBlue.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.02f))
                                                .border(
                                                    1.dp,
                                                    if (isSelected) AccentBlue else Color.White.copy(alpha = 0.08f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedRegion = regionCode }
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = regionLabel,
                                                color = if (isSelected) TextPrimary else TextSecondary,
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isSelected) {
                                                Text("✓", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Error message if any
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = AccentRed,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        if (currentStep > 1) {
                            Button(
                                onClick = { currentStep-- },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Back", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(80.dp))
                        }

                        // Next / Save Button
                        Button(
                            onClick = {
                                if (currentStep < 3) {
                                    currentStep++
                                } else {
                                    viewModel.savePreferences(
                                        region = selectedRegion,
                                        interests = selectedGenres.toList(),
                                        languages = selectedLanguages.toList(),
                                        onComplete = onOnboardingCompleted
                                    )
                                }
                            },
                            enabled = !uiState.isLoading && (
                                (currentStep == 1 && selectedGenres.isNotEmpty()) ||
                                (currentStep == 2 && selectedLanguages.isNotEmpty()) ||
                                (currentStep == 3)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                contentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            ),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = if (currentStep == 3) "Finish" else "Next",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferenceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) AccentBlue else Color.White.copy(alpha = 0.03f)
    val borderColor = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.08f)
    val textColor = if (isSelected) Color.White else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(containerColor)
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(30.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
