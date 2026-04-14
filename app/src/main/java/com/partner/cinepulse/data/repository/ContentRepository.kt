package com.partner.cinepulse.data.repository

import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.remote.models.crewResponse
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.reviewResponse
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.remote.models.tvshowResponse
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    suspend fun searchContent(q:String) : Flow<Resource<searchResponse>>
    suspend fun getActor(actor_id: Int) : Flow<Resource<actorResponse>>

    suspend fun getCrew(crew_id : Int) : Flow<Resource<crewResponse>>
    suspend fun getMovie(movieId : Int) : Flow<Resource<movieResponse>>
    suspend fun getTvShow(tvShowId : Int) : Flow<Resource<tvshowResponse>>
    suspend fun postReview(movieId : Int,request: reviewRequest) : Flow<Resource<reviewResponse>>



}