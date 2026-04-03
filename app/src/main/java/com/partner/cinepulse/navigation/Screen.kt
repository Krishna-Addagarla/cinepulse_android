package com.partner.cinepulse.navigation
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Reviews : Screen("reviews")
    object Discussions : Screen("discussions")
    object Chatbot : Screen("chatbot")
}