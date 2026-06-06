package com.partner.cinepulse.ui.screens.userInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.*

@Composable
fun UserInfoScreen(
    onBackClick: () -> Unit,
    onLogOut : () -> Unit,
    viewModel: UserInfoViewModel = hiltViewModel()
) {
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
                        model = "",
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
                        .clickable { /* onEditProfile */ },
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

            // User Name & Email placeholders
            Text(
                text = "Krishna",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "krishna@email.com",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Activity Section
            SectionLabel("Activity")

            ProfileMenuItem(
                icon = Icons.Default.Star,
                label = "Your Reviews",
                iconTint = PrimaryColor,
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Article,
                label = "Your Posts",
                iconTint = PrimaryColor,
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.List,
                label = "Your Lists",
                iconTint = PrimaryColor,
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                label = "Your Favorites",
                iconTint = PrimaryColor,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Settings Section
            SectionLabel("Settings")

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                iconTint = TextSecondary,
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                iconTint = TextSecondary,
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                label = "Privacy & Security",
                iconTint = TextSecondary,
                onClick = {}
            )

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
            .background(CardDark)
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
        onLogOut = {}
    )
}