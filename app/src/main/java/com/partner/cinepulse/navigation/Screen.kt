package com.partner.cinepulse.navigation

import android.net.Uri

sealed class Screen(val route: String) {

    object Auth : Screen("auth")
    object Otp : Screen("otp/{email}"){
        fun createRoute(email: String) = "otp/${Uri.encode(email)}"
    }
    object Home : Screen("home")
    object Search : Screen("search")
    object Reviews : Screen("reviews")
    object Discussions : Screen("discussions")
    object Chatbot : Screen("chatbot")
    object Profile : Screen("profile")
}