package com.partner.cinepulse.data.remote.models

import java.time.OffsetDateTime


data class createFanClub(
    val name: String,
    val description: String,
    val photoUrl: String,
    val coverUrl: String,
    val isPrivate: Boolean,
    val actorId: Int,
    val movieId: Int,
    val tvShowId: Int,
    val crewId: Int
)

data class createFanClubResponse(
    val name: String,
    val description: String,
    val photoUrl: String,
    val coverUrl: String,
    val isPrivate: Boolean,
    val id: Int,
    val createdBy: Int,
    val creatorName: String,
    val membersCount: Int,
    val postsCount: Int,
    val isMember: Boolean,
    val isAdmin: Boolean,
    val hasPendingRequest: Boolean,
    val createdAt: String,  // or Date type if you parse it
    val linkedActor: List<Int>,
    val linkedMovie: List<Int>,
    val linkedTvShow: List<Int>,
    val linkedCrew: List<Int>
)

data class FanClubResponse(
    val name: String,
    val description: String,
    val photoUrl: String,
    val coverUrl: String,
    val isPrivate: Boolean,
    val id: Long,
    val createdBy: Long,
    val creatorName: String,
    val membersCount: Int,
    val postsCount: Int,
    val isMember: Boolean,
    val isAdmin: Boolean,
    val hasPendingRequest: Boolean,
    val createdAt: OffsetDateTime,
    val linkedActor: Any? = null,
    val linkedMovie: Any? = null,
    val linkedTvshow: Any? = null,
    val linkedCrew: Any? = null
)
