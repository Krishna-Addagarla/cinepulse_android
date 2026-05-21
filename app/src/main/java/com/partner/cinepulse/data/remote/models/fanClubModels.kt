package com.partner.cinepulse.data.remote.models


data class createFanClub(
    val name: String,
    val description: String,
    val photo_url: String,
    val cover_url: String,
    val is_private: Boolean,
    val artist_id: Int,
    val movie_id: Int,
    val tvShow_id: Int
)

data class createFanClubResponse(
    val name: String,
    val description: String,
    val photo_url: String,
    val cover_url: String,
    val is_private: Boolean,
    val id: Int,
    val createdBy: Int,
    val creatorName: String,
    val membersCount: Int,
    val postsCount: Int,
    val is_member: Boolean,
    val is_admin: Boolean,
    val hasPendingRequest: Boolean,
    val created_at: String,  // or Date type if you parse it
    val linked_artist: List<Int>,
    val linked_movie: List<Int>,
    val linked_tvshow: List<Int>
)

data class FanClubResponse(
    val name: String,
    val description: String,
    val photo_url: String,
    val cover_url: String,
    val is_private: Boolean,
    val id: Long,
    val created_by: Long,
    val creator_name: String,
    val members_count: Int,
    val posts_count: Int,
    val is_member: Boolean,
    val is_admin: Boolean,
    val has_pending_request: Boolean,
    val created_at: String,
    val linked_actor: Any? = null,
    val linked_movie: Any? = null,
    val linked_tvshow: Any? = null,
    val linked_crew: Any? = null
)
