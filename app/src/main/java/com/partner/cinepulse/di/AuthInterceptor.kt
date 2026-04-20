package com.partner.cinepulse.di

import com.partner.cinepulse.data.remote.apis.authApiService
import com.partner.cinepulse.data.remote.models.refreshRequest
import com.partner.cinepulse.data.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    val tokenRepository: TokenRepository,
    val authApiService: authApiService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response = chain.proceed(authenticatedRequest(request))

        if (response.code == 401){
            synchronized(this){
                val refreshtoken = runBlocking { refreshToken() }

                if (refreshtoken!= null){
                    response.close()
                    request = authenticatedRequest(request)
                    response = chain.proceed(request)
                }
            }
        }
        return response
    }

    private fun authenticatedRequest(request : Request) : Request{
        val token = runBlocking { tokenRepository.getAccessToken() }

        return if (!token.isNullOrEmpty()){
            request.newBuilder().addHeader("Authorization","Bearer $token").build()
        }else{
            request
        }
    }

    private suspend fun refreshToken(): String? {
        return try {
            val response = authApiService.refreshToken(
                refreshRequest(tokenRepository.getRefreshToken() ?: "")
            )
            if (response.isSuccessful) {
                val newToken = response.body()?.access_token
                val refreshToken = response.body()?.refresh_token
                if (newToken!=null && refreshToken!=null){
                    tokenRepository.saveTokens(newToken,refreshToken)
                }
                newToken
            } else {
                tokenRepository.clearTokens()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}