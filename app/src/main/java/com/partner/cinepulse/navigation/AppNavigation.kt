package com.partner.cinepulse.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.partner.cinepulse.ui.screens.chatbot.ChatbotScreen
import com.partner.cinepulse.ui.screens.discussion.DiscussionsScreen
import com.partner.cinepulse.ui.screens.home.HomeScreen
import com.partner.cinepulse.ui.screens.reviews.ReviewsScreen
import com.partner.cinepulse.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(
    navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
//        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToReviews = {
                    navController.navigate(Screen.Reviews.route)
                },
                onNavigateToDiscussions = {
                    navController.navigate(Screen.Discussions.route)
                },
                onNavigateToChatbot = {
                    navController.navigate(Screen.Chatbot.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Reviews.route) {
            ReviewsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Discussions.route) {
            DiscussionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Chatbot.route) {
            ChatbotScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}