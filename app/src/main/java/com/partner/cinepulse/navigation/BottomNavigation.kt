package com.partner.cinepulse.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.partner.cinepulse.ui.theme.PrimaryColor
import com.partner.cinepulse.ui.theme.tertiaryColor

@Composable
fun BottomNavigation(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = tertiaryColor,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Screen.Home to "Home",
            Screen.Search to "Search",
            Screen.Reviews to "Reviews",
            Screen.Discussions to "Discussions",
            Screen.Chatbot to "Chatbot"
        )

        items.forEach { (screen, title) ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
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
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (currentRoute == screen.route)
                            FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE50914),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFFE50914),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}