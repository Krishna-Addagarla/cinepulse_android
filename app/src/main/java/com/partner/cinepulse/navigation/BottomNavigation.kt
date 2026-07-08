package com.partner.cinepulse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.partner.cinepulse.ui.theme.PrimaryColor
import com.partner.cinepulse.ui.theme.tertiaryColor
import com.partner.cinepulse.ui.theme.AccentBlue

@Composable
fun BottomNavigation(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
        containerColor = Color(0xEC0B0F19), // Match BgDark with transparency
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0.dp) // Reset bottom system bar padding
    ) {
        val items = listOf(
            Triple(Screen.Home, "Home", Icons.Default.Home),
            Triple(Screen.Search, "Search", Icons.Default.Search),
            Triple(Screen.Reviews, "Reviews", Icons.Default.Star),
            Triple(Screen.Discussions, "Discussions", Icons.Default.List),
            Triple(Screen.Chatbot, "Chatbot", Icons.Default.Build)
        )

        items.forEach { (screen, title, iconImage) ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = iconImage,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = AccentBlue,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}