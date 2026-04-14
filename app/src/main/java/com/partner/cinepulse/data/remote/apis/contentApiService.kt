package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.remote.models.crewResponse
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.reviewResponse
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.remote.models.tvshowResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface contentApiService {

    @GET("content/search")
    suspend fun searchContent(
       @Query("q") queryContent : String
    ) : Response<searchResponse>

    @GET("content/actors/{actor_id}")
    suspend fun getActor(
        @Path("actor_id") actorId : Int
    ) : Response<actorResponse>

    @GET("content/crew/{crew_id}")
    suspend fun getCrew(
        @Path("crew_id") crewId : Int
    ) : Response<crewResponse>

    @GET("content/movies/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieId : Int
    ) : Response<movieResponse>

    @GET("content/tvshows/{tvshow_id}")
    suspend fun getTvShow(
        @Path("tvshow_id") tvshowId : Int
    ) : Response<tvshowResponse>

    @POST("content/movies/{movie_id}/reviews")
    suspend fun postReview(
        @Path("movie_id") movieId : Int,
        @Body request: reviewRequest
    ) : Response<reviewResponse>
}