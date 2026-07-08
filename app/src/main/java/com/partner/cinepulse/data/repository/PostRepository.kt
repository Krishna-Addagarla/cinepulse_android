package com.partner.cinepulse.data.repository

import com.partner.cinepulse.data.remote.models.ExploreActivityResponse
import com.partner.cinepulse.data.remote.models.PostCreateRequest
import com.partner.cinepulse.data.remote.models.PostResponse
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun getExploreFeed(skip: Int, limit: Int): Flow<Resource<List<ExploreActivityResponse>>>
    suspend fun createPost(request: PostCreateRequest): Flow<Resource<PostResponse>>
    suspend fun getUserPosts(userId: Int, skip: Int, limit: Int): Flow<Resource<List<PostResponse>>>
    suspend fun getUserReviews(userId: Int): Flow<Resource<List<ExploreActivityResponse>>>
}
