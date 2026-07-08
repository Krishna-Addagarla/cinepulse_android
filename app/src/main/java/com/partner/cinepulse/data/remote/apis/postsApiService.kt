package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.ExploreActivityResponse
import com.partner.cinepulse.data.remote.models.PostCreateRequest
import com.partner.cinepulse.data.remote.models.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface postsApiService {

    @GET("posts/explore")
    suspend fun getExploreFeed(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): Response<List<ExploreActivityResponse>>

    @POST("posts/")
    suspend fun createPost(
        @Body request: PostCreateRequest
    ): Response<PostResponse>

    @GET("posts/user/{user_id}")
    suspend fun getUserPosts(
        @retrofit2.http.Path("user_id") userId: Int,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): Response<List<PostResponse>>

    @GET("posts/user/{user_id}/reviews")
    suspend fun getUserReviews(
        @retrofit2.http.Path("user_id") userId: Int
    ): Response<List<ExploreActivityResponse>>
}