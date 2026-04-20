package com.partner.cinepulse.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.partner.cinepulse.data.repository.TokenRepository
import com.partner.cinepulse.ui.screens.auth.AuthScreen
import com.partner.cinepulse.ui.screens.auth.OtpVerificationScreen
import com.partner.cinepulse.ui.screens.chatbot.ChatbotScreen
import com.partner.cinepulse.ui.screens.fanclub.DiscussionsScreen
import com.partner.cinepulse.ui.screens.home.HomeScreen
import com.partner.cinepulse.ui.screens.reviews.ReviewsScreen
import com.partner.cinepulse.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenRepository: TokenRepository) {

    var isLoggedIn by remember{ mutableStateOf(false)}
    var isChecking by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoggedIn = tokenRepository.isLoggedIn()
        isChecking = false
    }

    if (isChecking){
        CircularProgressIndicator()
    }else{
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Auth.route
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onSignInSuccess = {
                        navController.navigate(Screen.Home.route)
                    },
                    onSignUpSuccess = {email ->
                        navController.navigate(Screen.Otp.createRoute(email))
                    }
                )

            }
            composable(Screen.Otp.route,
                arguments = listOf(navArgument("email"){type = NavType.StringType})) {
                    backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")?:""
                OtpVerificationScreen(
                    email = email,
                    onVerificationSuccess = {
                        navController.navigate(Screen.Home.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

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

}