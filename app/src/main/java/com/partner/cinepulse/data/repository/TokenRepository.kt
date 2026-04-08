package com.partner.cinepulse.data.repository


import com.partner.cinepulse.data.local.dao.TokenDao
import com.partner.cinepulse.data.local.entity.TokenEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepository @Inject constructor(
    private val tokenDao: TokenDao
) {

    // Save tokens (upsert)
    suspend fun saveTokens(accessToken: String, refreshToken: String, userId: Int? = null) {
        tokenDao.upsertTokens(
            TokenEntity(
                id = 1,
                access_token = accessToken,
                refresh_token = refreshToken,
                userId = userId
            )
        )
    }

    // Get just the access token (one-time)
    suspend fun getAccessToken(): String? = tokenDao.getAccessToken()

    // Observe access token changes (for real-time UI updates)
    fun observeAccessToken(): Flow<String?> = tokenDao.getAccessTokenFlow()

    // Check if user is logged in
    suspend fun isLoggedIn(): Boolean = tokenDao.isLoggedIn()

    // Clear all tokens
    suspend fun logout() {
        tokenDao.clearTokens()
    }
}