package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.loginRequest
import com.partner.cinepulse.data.remote.models.otpVerificationRequest
import com.partner.cinepulse.data.remote.models.refreshRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.data.remote.models.registrationResponse
import com.partner.cinepulse.data.remote.models.resendResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.verifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface authApiService {

    @POST("auth/register")
    suspend fun registerUser(
        @Body request: registrationRequest
    ) : Response<registrationResponse>

    @POST("auth/login")
    suspend fun loginUser(
        @Body request: loginRequest
    ): Response<verifyResponse>

    @POST("auth/verify")
    suspend fun verifyOTP(
        @Body request : otpVerificationRequest
    ): Response<verifyResponse>

    @POST("auth/resend-otp")
    suspend fun resendOTP(
        @Body email : String
    ) : Response<resendResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: refreshRequest
    ) : Response<verifyResponse>
}