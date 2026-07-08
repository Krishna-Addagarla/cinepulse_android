package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.loginRequest
import com.partner.cinepulse.data.remote.models.otpVerificationRequest
import com.partner.cinepulse.data.remote.models.refreshRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.data.remote.models.registrationResponse
import com.partner.cinepulse.data.remote.models.resendResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.verifyResponse
import com.partner.cinepulse.data.remote.models.userResponse
import com.partner.cinepulse.data.remote.models.userProfileUpdateRequest
import com.partner.cinepulse.data.remote.models.UsernameCheckResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part

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

    @GET("auth/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<userResponse>

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: userProfileUpdateRequest
    ): Response<userResponse>

    @GET("auth/check-username")
    suspend fun checkUsername(
        @Header("Authorization") token: String,
        @Query("username") username: String
    ): Response<UsernameCheckResponse>

    @Multipart
    @POST("auth/profile/upload-avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<userResponse>
}