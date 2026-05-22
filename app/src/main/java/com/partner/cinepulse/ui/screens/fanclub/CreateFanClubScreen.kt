package com.partner.cinepulse.ui.screens.fanclub

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.createFanClub
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.utils.ImagePickerState
import com.partner.cinepulse.utils.rememberImagePicker

private val AccentColor = Color(0xFFE5A100)
private val SubtleText  = Color(0xFF9E9E9E)
private val InputBg     = Color(0xFF1C1C1E)
private val ChipBg      = Color(0xFF2A2A2D)

// ── entry point ───────────────────────────────────────────────────────────
@Composable
fun CreateFanClubScreen(
    onNavigateBack: () -> Unit,
    viewModel: FanClubViewModel = hiltViewModel()
) {
    CreateFanClubScreenContent(
        onNavigateBack = onNavigateBack,
        viewModel      = viewModel
    )
}

// ── screen ────────────────────────────────────────────────────────────────
@Composable
fun CreateFanClubScreenContent(
    onNavigateBack: () -> Unit,
    viewModel: FanClubViewModel? = null
) {
    val context = LocalContext.current

    // ── form state ──
    var fanClubName        by remember { mutableStateOf("") }
    var fanClubDescription by remember { mutableStateOf("") }
    var tagInput           by remember { mutableStateOf("") }
    val tags               = remember { mutableStateListOf<String>() }

    // ── pending upload tracking ──
    var pendingProfileUrl by remember { mutableStateOf<String?>(null) }
    var pendingCoverUrl   by remember { mutableStateOf<String?>(null) }
    var submitRequested   by remember { mutableStateOf(false) }
    var lastUploadTag     by remember { mutableStateOf<String?>(null) }

    // ── ViewModel state ──
    val uploadImage by viewModel?.uploadImage?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val uiState by viewModel?.uistate?.collectAsState()
        ?: remember { mutableStateOf(FanClubUiState()) }

    // ── ONE-SHOT navigation: collect from Channel, not a StateFlow ──
    // This fires exactly once per successful creation, regardless of recomposition.
    LaunchedEffect(viewModel) {
        viewModel?.navigateBack?.collect {
            Toast.makeText(context, "FanClub created successfully!", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    // ── image pickers ──
    val profilePicker = rememberImagePicker(
        onUpload = { multipart ->
            lastUploadTag = "profile"
            viewModel?.uploadImage(multipart)
            Result.success("")
        }
    )
    val coverPicker = rememberImagePicker(
        onUpload = { multipart ->
            lastUploadTag = "cover"
            viewModel?.uploadImage(multipart)
            Result.success("")
        }
    )

    // ── react to upload result ──
    LaunchedEffect(uploadImage) {
        val url = uploadImage?.image_url ?: return@LaunchedEffect
        when (lastUploadTag) {
            "profile" -> pendingProfileUrl = url
            "cover"   -> pendingCoverUrl   = url
        }
        lastUploadTag = null
        viewModel?.clearUploadImage()

        trySubmitIfReady(
            submitRequested       = submitRequested,
            pendingProfileUrl     = pendingProfileUrl,
            pendingCoverUrl       = pendingCoverUrl,
            profilePickerHasImage = profilePicker.state.hasImage,
            coverPickerHasImage   = coverPicker.state.hasImage,
            onReady               = { _, _ ->
                submitRequested = false   // prevent double-fire
                viewModel?.createFanClub(
                    createFanClub(
                        name        = fanClubName,
                        description = fanClubDescription,
                        photo_url   = pendingProfileUrl ?: "",
                        cover_url   = pendingCoverUrl   ?: "",
                        is_private  = false,
                        artist_id   = 0,
                        movie_id    = 0,
                        tvShow_id   = 0
                    )
                )
            }
        )
    }

    // ── error toast ──
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── top bar ──
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
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = TextPrimary,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Text(
                text       = "Create a FanClub",
                color      = TextPrimary,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ── scrollable form ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionLabel("Cover Picture")
            CoverPicturePicker(
                state   = coverPicker.state,
                onPick  = { coverPicker.launch() },
                onClear = { coverPicker.clear(); pendingCoverUrl = null }
            )
            UploadStatusRow(coverPicker.state)

            SectionLabel("Profile Picture")
            ProfilePicturePicker(
                state   = profilePicker.state,
                onPick  = { profilePicker.launch() },
                onClear = { profilePicker.clear(); pendingProfileUrl = null }
            )
            UploadStatusRow(profilePicker.state)

            SectionLabel("FanClub Name")
            StyledTextField(
                value         = fanClubName,
                onValueChange = { fanClubName = it },
                placeholder   = "e.g. House of Stark Fans"
            )

            SectionLabel("Description")
            StyledTextField(
                value         = fanClubDescription,
                onValueChange = { fanClubDescription = it },
                placeholder   = "Tell fans what this club is about…",
                minLines      = 4,
                maxLines      = 6
            )

            SectionLabel("Tag Artist / Movie / TV Show  (optional)")
            TagInputField(
                input    = tagInput,
                tags     = tags,
                onInput  = { tagInput = it },
                onAddTag = {
                    val trimmed = tagInput.trim()
                    if (trimmed.isNotEmpty() && !tags.contains(trimmed)) tags.add(trimmed)
                    tagInput = ""
                },
                onRemove = { tags.remove(it) }
            )

            Button(
                onClick = {
                    if (fanClubName.isBlank()) {
                        Toast.makeText(context, "Please enter a FanClub name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    submitRequested   = true
                    pendingProfileUrl = null
                    pendingCoverUrl   = null

                    val needsProfileUpload = profilePicker.state.hasImage
                    val needsCoverUpload   = coverPicker.state.hasImage

                    if (needsProfileUpload) profilePicker.upload()
                    if (needsCoverUpload)   coverPicker.upload()

                    if (!needsProfileUpload && !needsCoverUpload) {
                        submitRequested = false   // won't go through LaunchedEffect path
                        viewModel?.createFanClub(
                            createFanClub(
                                name        = fanClubName,
                                description = fanClubDescription,
                                photo_url   = "",
                                cover_url   = "",
                                is_private  = false,
                                artist_id   = 0,
                                movie_id    = 0,
                                tvShow_id   = 0
                            )
                        )
                    }
                },
                // Disable only while the create/upload is in progress, not during list fetch.
                enabled  = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Create FanClub",
                        color      = TextPrimary,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── submit gate ───────────────────────────────────────────────────────────
private fun trySubmitIfReady(
    submitRequested      : Boolean,
    pendingProfileUrl    : String?,
    pendingCoverUrl      : String?,
    profilePickerHasImage: Boolean,
    coverPickerHasImage  : Boolean,
    onReady              : (profileUrl: String, coverUrl: String) -> Unit
) {
    if (!submitRequested) return
    val profileReady = !profilePickerHasImage || pendingProfileUrl != null
    val coverReady   = !coverPickerHasImage   || pendingCoverUrl   != null
    if (profileReady && coverReady) {
        onReady(pendingProfileUrl ?: "", pendingCoverUrl ?: "")
    }
}

// ── cover picture picker UI ───────────────────────────────────────────────
@Composable
private fun CoverPicturePicker(
    state   : ImagePickerState,
    onPick  : () -> Unit,
    onClear : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(InputBg)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onPick() },
        contentAlignment = Alignment.Center
    ) {
        if (state.uri != null) {
            AsyncImage(
                model              = state.uri,
                contentDescription = "Cover Picture",
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Tap to change", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onClear() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(6.dp))
                Text("Upload Cover Picture", color = SubtleText, fontSize = 14.sp)
                Text("Recommended 16 : 9", color = SubtleText.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

// ── profile picture picker UI ─────────────────────────────────────────────
@Composable
private fun ProfilePicturePicker(
    state   : ImagePickerState,
    onPick  : () -> Unit,
    onClear : () -> Unit
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(InputBg)
                .border(2.dp, if (state.hasImage) AccentBlue else CardBorder, CircleShape)
                .clickable { onPick() },
            contentAlignment = Alignment.Center
        ) {
            if (state.uri != null) {
                AsyncImage(
                    model              = state.uri,
                    contentDescription = "Profile Picture",
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Add, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(28.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = if (state.hasImage) "Profile picture set" else "Upload Profile Picture",
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text     = "Tap the circle to ${if (state.hasImage) "change" else "upload"}",
                color    = SubtleText,
                fontSize = 12.sp
            )
            if (state.hasImage) {
                Text(
                    text     = "Clear",
                    color    = AccentBlue,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onClear() }
                )
            }
        }
    }
}

// ── upload status indicator ───────────────────────────────────────────────
@Composable
private fun UploadStatusRow(state: ImagePickerState) {
    when {
        state.isUploading -> Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                modifier    = Modifier.size(14.dp),
                color       = AccentBlue,
                strokeWidth = 2.dp
            )
            Text("Uploading…", color = SubtleText, fontSize = 12.sp)
        }
        state.uploadError != null -> Text(
            text     = "⚠ ${state.uploadError}",
            color    = Color(0xFFEF5350),
            fontSize = 12.sp
        )
        state.uploadedUrl?.isNotEmpty() == true -> Text(
            text     = "✓ Uploaded successfully",
            color    = Color(0xFF66BB6A),
            fontSize = 12.sp
        )
    }
}

// ── tag input + chips ─────────────────────────────────────────────────────
@Composable
private fun TagInputField(
    input    : String,
    tags     : List<String>,
    onInput  : (String) -> Unit,
    onAddTag : () -> Unit,
    onRemove : (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = input,
                onValueChange = onInput,
                modifier      = Modifier.weight(1f),
                placeholder   = { Text("e.g. BTS, Inception, Breaking Bad", color = SubtleText, fontSize = 13.sp) },
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = AccentBlue,
                    unfocusedBorderColor    = CardBorder,
                    focusedTextColor        = TextPrimary,
                    unfocusedTextColor      = TextPrimary,
                    cursorColor             = AccentBlue,
                    focusedContainerColor   = InputBg,
                    unfocusedContainerColor = InputBg
                ),
                singleLine = true
            )
            Button(
                onClick        = onAddTag,
                enabled        = input.trim().isNotEmpty(),
                shape          = RoundedCornerShape(12.dp),
                colors         = ButtonDefaults.buttonColors(
                    containerColor         = AccentBlue,
                    disabledContainerColor = CardDark
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text("Add", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
        }

        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    TagChip(label = tag, onRemove = { onRemove(tag) })
                }
            }
        }
    }
}

@Composable
private fun TagChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(ChipBg)
            .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Icon(
            imageVector        = Icons.Default.Close,
            contentDescription = "Remove $label",
            tint               = SubtleText,
            modifier           = Modifier
                .size(14.dp)
                .clickable { onRemove() }
        )
    }
}

// ── reusable text field ───────────────────────────────────────────────────
@Composable
private fun StyledTextField(
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String,
    minLines      : Int = 1,
    maxLines      : Int = 1
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = Modifier.fillMaxWidth(),
        placeholder   = { Text(placeholder, color = SubtleText, fontSize = 14.sp) },
        shape         = RoundedCornerShape(12.dp),
        minLines      = minLines,
        maxLines      = maxLines,
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = AccentBlue,
            unfocusedBorderColor    = CardBorder,
            focusedTextColor        = TextPrimary,
            unfocusedTextColor      = TextPrimary,
            cursorColor             = AccentBlue,
            focusedContainerColor   = InputBg,
            unfocusedContainerColor = InputBg
        )
    )
}

// ── section label ─────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        color      = TextPrimary,
        fontSize   = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
}

// ── preview ───────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun CreateFanClubPreview() {
    CreateFanClubScreenContent(onNavigateBack = {}, viewModel = null)
}