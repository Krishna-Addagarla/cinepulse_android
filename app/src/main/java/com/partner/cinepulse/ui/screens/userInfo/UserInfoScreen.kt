package com.partner.cinepulse.ui.screens.userInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    onBackClick: () -> Unit,
    onLogOut : () -> Unit,
    onEditPreferences: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToLists: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: UserInfoViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val favoritesCount by viewModel.favoritesCount.collectAsStateWithLifecycle()
    val listsCount by viewModel.listsCount.collectAsStateWithLifecycle()
    val reviewsList by viewModel.userReviews.collectAsStateWithLifecycle()
    val reviewsCount = reviewsList.size
    val userPosts by viewModel.userPosts.collectAsStateWithLifecycle()

    var showPostsSheet by remember { mutableStateOf(false) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editUsername by remember { mutableStateOf("") }
    var editUsernameError by remember { mutableStateOf<String?>(null) }
    var editAvatarUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var editAvatarError by remember { mutableStateOf<String?>(null) }
    var isSavingProfile by remember { mutableStateOf(false) }
    var editProfileGeneralError by remember { mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val sizeBytes = inputStream?.available() ?: 0
                inputStream?.close()
                if (sizeBytes > 5 * 1024 * 1024) {
                    editAvatarError = "Image must be under 5MB"
                } else {
                    editAvatarUri = uri
                    editAvatarError = null
                }
            } catch (e: Exception) {
                editAvatarError = "Failed to load image: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUserProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopBar(
            title = "Profile",
            showBackButton = true,
            showNotificationIcon = false,
            showProfileIcon = false,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Avatar with Edit Icon
            Box(
                modifier = Modifier.size(108.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .border(2.dp, CardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = userProfile?.photo_url ?: "",
                        contentDescription = "User Profile",
                        modifier = Modifier.fillMaxSize(),
                        alignment = Alignment.Center
                    )
                }

                // Edit Icon Badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor)
                        .border(2.dp, BgDark, CircleShape)
                        .clickable {
                            editUsername = userProfile?.username ?: ""
                            editUsernameError = null
                            editAvatarUri = null
                            editAvatarError = null
                            editProfileGeneralError = null
                            showEditProfileDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User Name dynamically loaded
            Text(
                text = userProfile?.username ?: "Krishna",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Activity Section
            SectionLabel("Activity")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Reviews",
                    count = reviewsCount,
                    icon = Icons.Default.Star,
                    color = AccentGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReviews
                )
                StatsCard(
                    title = "Posts",
                    count = userPosts.size,
                    icon = Icons.Default.Article,
                    color = AccentBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { showPostsSheet = true }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Lists",
                    count = listsCount,
                    icon = Icons.Default.List,
                    color = AccentGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLists
                )
                StatsCard(
                    title = "Favorites",
                    count = favoritesCount,
                    icon = Icons.Default.Favorite,
                    color = AccentRed,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToFavorites
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Settings Section
            SectionLabel("Settings")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xDC0F1623))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Edit,
                        label = "Edit Interests & Language",
                        iconTint = AccentBlue,
                        onClick = onEditPreferences
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 0.5.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        iconTint = TextSecondary,
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 0.5.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        iconTint = TextSecondary,
                        onClick = {}
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 0.5.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        label = "Privacy & Security",
                        iconTint = TextSecondary,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button pinned to bottom
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = {viewModel.logout(onLogOut)} ,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = AccentRed,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 6.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Log Out",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSavingProfile) showEditProfileDialog = false },
            title = { Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(CardDark)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (editAvatarUri != null) {
                            AsyncImage(
                                model = editAvatarUri,
                                contentDescription = "New Profile Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (!userProfile?.photo_url.isNullOrEmpty()) {
                            AsyncImage(
                                model = userProfile?.photo_url,
                                contentDescription = "Current Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Photo",
                                tint = AccentBlue,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    if (editAvatarError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(editAvatarError!!, color = AccentRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = {
                            editUsername = it.trim()
                            editUsernameError = when {
                                editUsername.length < 3 -> "Username must be at least 3 characters"
                                editUsername.length > 20 -> "Username must be at most 20 characters"
                                !editUsername.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Only letters, numbers, and underscores allowed"
                                else -> null
                            }
                        },
                        label = { Text("Username") },
                        isError = editUsernameError != null,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = CardBorder,
                            errorBorderColor = AccentRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (editUsernameError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(editUsernameError!!, color = AccentRed, fontSize = 12.sp)
                    }

                    if (editProfileGeneralError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(editProfileGeneralError!!, color = AccentRed, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editUsernameError == null && editUsername != userProfile?.username) {
                            isSavingProfile = true
                            viewModel.checkUsername(editUsername) { isTaken ->
                                if (isTaken) {
                                    editUsernameError = "Username is already taken"
                                    isSavingProfile = false
                                } else {
                                    saveProfileChanges(
                                        context, editAvatarUri, editUsername, viewModel,
                                        onSuccess = {
                                            isSavingProfile = false
                                            showEditProfileDialog = false
                                        },
                                        onError = {
                                            editProfileGeneralError = it
                                            isSavingProfile = false
                                        }
                                    )
                                }
                            }
                        } else if (editUsernameError == null) {
                            isSavingProfile = true
                            saveProfileChanges(
                                context, editAvatarUri, null, viewModel,
                                onSuccess = {
                                    isSavingProfile = false
                                    showEditProfileDialog = false
                                },
                                onError = {
                                    editProfileGeneralError = it
                                    isSavingProfile = false
                                }
                            )
                        }
                    },
                    enabled = !isSavingProfile && editUsernameError == null && editAvatarError == null,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    if (isSavingProfile) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("Finish")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditProfileDialog = false },
                    enabled = !isSavingProfile
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardDark
        )
    }

    if (showPostsSheet) {
        val userPosts by viewModel.userPosts.collectAsStateWithLifecycle()
        ModalBottomSheet(
            onDismissRequest = { showPostsSheet = false },
            containerColor = CardDark,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Your Posts",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (userPosts.isEmpty()) {
                    Text(
                        text = "You haven't posted anything yet.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userPosts) { post ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = BgDark),
                                border = BorderStroke(1.dp, CardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = post.content,
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (!post.tagged_movies.isNullOrEmpty() || !post.tagged_artists.isNullOrEmpty() || !post.tagged_tvshows.isNullOrEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                                        ) {
                                            post.tagged_movies?.forEach { movie ->
                                                TagDisplayChip(label = "🎬 ${movie.title}")
                                            }
                                            post.tagged_tvshows?.forEach { tv ->
                                                TagDisplayChip(label = "📺 ${tv.title}")
                                            }
                                            post.tagged_artists?.forEach { artist ->
                                                TagDisplayChip(label = "👤 ${artist.name}")
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = post.created_at.substringBefore("T"),
                                            color = TextSecondary.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Favorite,
                                                    contentDescription = "Likes",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${post.likes_count}",
                                                    color = TextSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Comment,
                                                    contentDescription = "Comments",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${post.comments_count}",
                                                    color = TextSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagDisplayChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CardBorder.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.Transparent)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }

    HorizontalDivider(
        color = CardBorder.copy(alpha = 0.5f),
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 74.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun UserInfoScreenPreview() {
    UserInfoScreen(
        onBackClick = {},
        onLogOut = {},
        onEditPreferences = {},
        onNavigateToFavorites = {},
        onNavigateToLists = {},
        onNavigateToReviews = {},
        onNavigateToSettings = {}
    )
}

@Composable
private fun StatsCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xDC0F1623))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = count.toString(),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun saveProfileChanges(
    context: android.content.Context,
    avatarUri: android.net.Uri?,
    username: String?,
    viewModel: UserInfoViewModel,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (avatarUri != null) {
        val part = createMultipartBodyPart(context, avatarUri, "file")
        if (part != null) {
            viewModel.uploadAvatar(part, onSuccess = {
                if (username != null) {
                    viewModel.updateProfile(username, onSuccess = onSuccess, onError = onError)
                } else {
                    onSuccess()
                }
            }, onError = onError)
        } else {
            onError("Failed to read image file")
        }
    } else if (username != null) {
        viewModel.updateProfile(username, onSuccess = onSuccess, onError = onError)
    } else {
        onSuccess()
    }
}

private fun createMultipartBodyPart(
    context: android.content.Context,
    uri: android.net.Uri,
    partName: String
): okhttp3.MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = java.io.File.createTempFile("avatar_", ".jpg", context.cacheDir)
        tempFile.deleteOnExit()
        val outputStream = java.io.FileOutputStream(tempFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        val requestBody = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
        okhttp3.MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    } catch (e: Exception) {
        null
    }
}