package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.data.remote.models.createFanClub
import com.partner.cinepulse.data.remote.models.createFanClubResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface fanClubApiService {

    @GET("fanclubs/user/joined")
    suspend fun getUserFanClubs(): Response<List<FanClubResponse>>

    @GET("fanclubs/{fanclub_id}")
    suspend fun getFanClub(
        @Path("fanclub_id") fanclubId : Int
    )

    @POST("/fanclubs")
    suspend fun createFanClub(
        @Body createFanClub: createFanClub
    ): Response<createFanClubResponse>
}