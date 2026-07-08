package com.partner.cinepulse.data.remote.models

import java.util.Date

data class registrationRequest(
    val email: String,
    val username: String,
    val password : String,
    val date_of_birth: String
)

data class userResponse(
    val id: Int,
    val email: String,
    val username: String?,
    val photo_url: String?,
    val is_verified: Boolean,
    val region: String?,
    val interests: List<String>?,
    val languages: List<String>?,
    val created_at: String
)

data class userProfileUpdateRequest(
    val region: String? = null,
    val interests: List<String>? = null,
    val languages: List<String>? = null,
    val username: String? = null,
    val photo_url: String? = null
)

data class UsernameCheckResponse(
    val is_taken: Boolean
)

data class registrationResponse(
    val message : String
)

data class loginRequest(
    val email: String,
    val password : String
)


data class otpVerificationRequest(
    val email : String,
    val otp : String
)

data class verifyResponse(
    val access_token : String,
    val refresh_token : String,
    val token_type : String
)

data class resendResponse(
    val message : String
)

data class refreshRequest(
    val refresh_token : String
)

