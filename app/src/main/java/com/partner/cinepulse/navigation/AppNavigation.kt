package com.partner.cinepulse.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.partner.cinepulse.data.repository.TokenRepository
import com.partner.cinepulse.ui.screens.actorinfo.ActorInfoScreen
import com.partner.cinepulse.ui.screens.auth.AuthScreen
import com.partner.cinepulse.ui.screens.auth.OtpVerificationScreen
import com.partner.cinepulse.ui.screens.chatbot.ChatbotScreen
import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.utils.Resource
import com.partner.cinepulse.ui.screens.fanclub.CreateFanClubScreen
import com.partner.cinepulse.ui.screens.fanclub.DiscussionsScreen
import com.partner.cinepulse.ui.screens.createpost.CreatePostScreen
import com.partner.cinepulse.ui.screens.home.HomeScreen
import com.partner.cinepulse.ui.screens.movieinfo.MovieInfoScreen
import com.partner.cinepulse.ui.screens.review.WriteReviewScreen
import com.partner.cinepulse.ui.screens.reviews.ReviewsScreen
import com.partner.cinepulse.ui.screens.search.SearchScreen
import com.partner.cinepulse.ui.screens.userInfo.UserInfoScreen
import com.partner.cinepulse.ui.screens.onboarding.OnboardingScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenRepository: TokenRepository,
    authRepository: AuthRepository
) {

    var isLoggedIn by remember{ mutableStateOf(false)}
    var startDestinationRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoggedIn = tokenRepository.isLoggedIn()
        if (isLoggedIn) {
            authRepository.getProfile().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val user = result.data
                        val hasOnboarded = !user.interests.isNullOrEmpty() && !user.languages.isNullOrEmpty()
                        if (hasOnboarded) {
                            startDestinationRoute = Screen.Home.route
                        } else {
                            startDestinationRoute = Screen.Onboarding.route
                        }
                    }
                    is Resource.Error -> {
                        startDestinationRoute = Screen.Home.route
                    }
                    else -> {}
                }
            }
        } else {
            startDestinationRoute = Screen.Auth.route
        }
    }

    if (startDestinationRoute == null){
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else{
        NavHost(
            navController = navController,
            startDestination = startDestinationRoute!!
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
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onMovieClick = {id->
                        navController.navigate(Screen.Movie.createRoute(id))
                    },
                    onArtistClick = {artistId->
                        navController.navigate(Screen.Actor.createRoute(artistId))
                    },
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToCreatePost = {
                        navController.navigate(Screen.CreatePost.route)
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToMovie = {id->
                        navController.navigate(Screen.Movie.createRoute(id))
                    },
                    onNavigateToActor = {id->
                        navController.navigate(Screen.Actor.createRoute(id))
                    },
                    onNavigateToFanclub = {}
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
                    },
                    onNavigateCreateFC = {
                        navController.navigate(Screen.CreateFanClub.route)
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

            composable(Screen.Profile.route) {
                UserInfoScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogOut = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditPreferences = {
                        navController.navigate(Screen.Onboarding.route)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    },
                    onNavigateToLists = {
                        navController.navigate(Screen.Lists.route)
                    },
                    onNavigateToReviews = {
                        navController.navigate(Screen.ReviewsList.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.AccountSettings.route)
                    }
                )
            }

            composable(Screen.Actor.route,
                arguments = listOf(navArgument("id"){type = NavType.IntType})){backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id")?:0
                    ActorInfoScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        id,
                        onMovieClick = {movieId ->
                            navController.navigate(Screen.Movie.createRoute(movieId))
                        }
                    )
            }

            composable(Screen.Movie.route,
                arguments = listOf(navArgument("id"){type = NavType.IntType})){backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")?:0
                MovieInfoScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    id,
                    onArtistClick ={artistId->
                        navController.navigate(Screen.Actor.createRoute(artistId))
                    },
                    onWriteReviewClick = {movieId->
                        navController.navigate(Screen.UserReview.createRoute(movieId))
                    }

                )
            }

            composable(Screen.UserReview.route,
                arguments = listOf(navArgument("id"){type= NavType.IntType})) { backStackEntry->
                val id = backStackEntry.arguments?.getInt("id",0)
                WriteReviewScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onReviewPosted = {
                        navController.popBackStack()
                    },
                    id
                )

            }

            composable(Screen.CreateFanClub.route) {
                CreateFanClubScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Favorites.route) {
                com.partner.cinepulse.ui.screens.favorites.UserFavoritesScreen(
                    onBackClick = { navController.popBackStack() },
                    onMovieClick = { id -> navController.navigate(Screen.Movie.createRoute(id)) },
                    onTvShowClick = { id -> navController.navigate(Screen.Movie.createRoute(id)) }
                )
            }

            composable(Screen.Lists.route) {
                com.partner.cinepulse.ui.screens.lists.UserListsScreen(
                    onBackClick = { navController.popBackStack() },
                    onCollectionClick = { id, name -> navController.navigate(Screen.CollectionDetails.createRoute(id, name)) }
                )
            }

            composable(Screen.CollectionDetails.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val name = backStackEntry.arguments?.getString("name") ?: ""
                com.partner.cinepulse.ui.screens.lists.CollectionDetailsScreen(
                    collectionId = id,
                    collectionName = name,
                    onBackClick = { navController.popBackStack() },
                    onMovieClick = { movieId -> navController.navigate(Screen.Movie.createRoute(movieId)) },
                    onTvShowClick = { tvShowId -> navController.navigate(Screen.Movie.createRoute(tvShowId)) }
                )
            }

            composable(Screen.ReviewsList.route) {
                com.partner.cinepulse.ui.screens.reviews.UserReviewsScreen(
                    onBackClick = { navController.popBackStack() },
                    onMovieClick = { id -> navController.navigate(Screen.Movie.createRoute(id)) },
                    onTvShowClick = { id -> navController.navigate(Screen.Movie.createRoute(id)) }
                )
            }

            composable(Screen.AccountSettings.route) {
                com.partner.cinepulse.ui.screens.account.AccountSettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onEditProfileClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingCompleted = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.CreatePost.route) {
                CreatePostScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }

}