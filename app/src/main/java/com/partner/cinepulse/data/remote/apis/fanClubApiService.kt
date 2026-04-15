package com.partner.cinepulse.data.remote.apis

import retrofit2.http.GET
import retrofit2.http.Path

interface fanClubApiService {

    @GET("fanclubs/{fanclub_id}")
    suspend fun getFanClub(
        @Path("fanclub_id") fanclubId : Int
    )
}