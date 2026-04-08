package com.partner.cinepulse.data.repository

import com.partner.cinepulse.data.remote.models.loginRequest
import com.partner.cinepulse.data.remote.models.otpVerificationRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.data.remote.models.registrationResponse
import com.partner.cinepulse.data.remote.models.resendResponse
import com.partner.cinepulse.data.remote.models.verifyResponse
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun registerUser(request: registrationRequest): Flow<Resource<registrationResponse>>
    suspend fun loginUser(request: loginRequest): Flow<Resource<verifyResponse>>
    suspend fun verifyOTP(request: otpVerificationRequest): Flow<Resource<verifyResponse>>
    suspend fun resendOTP(email: String): Flow<Resource<resendResponse>>

}