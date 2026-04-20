package com.partner.cinepulse.data.remote.models

import java.util.Date

data class registrationRequest(
    val email: String,
    val password : String,
    val date_of_birth: String
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

