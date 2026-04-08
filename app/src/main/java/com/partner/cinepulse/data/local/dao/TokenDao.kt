package com.partner.cinepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.partner.cinepulse.data.local.entity.TokenEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TokenDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTokens(tokens: TokenEntity)

    @Query("Select access_token from tokens where id = 1")
    suspend fun getAccessToken(): String

    @Query("Select refresh_token from tokens where id = 1")
    suspend fun getRefreshToken(): String

    @Query("Delete From tokens")
    suspend fun clearTokens()

    @Query("SELECT access_token FROM tokens WHERE id = 1")
    fun getAccessTokenFlow(): Flow<String?>

    @Query("SELECT EXISTS(SELECT 1 FROM tokens WHERE id = 1 AND access_token IS NOT NULL)")
    suspend fun isLoggedIn(): Boolean
}