package com.partner.cinepulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tokens")
data class TokenEntity(
    @PrimaryKey
    val id : Int = 1,
    val access_token : String,
    val refresh_token : String,
    val expiresAt: Long? = null, // Optional: token expiry timestamp
    val userId: Int? = null,
    val updatedAt: Long = System.currentTimeMillis()
)