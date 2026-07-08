package com.partner.cinepulse.data.repositoryImpl

import com.partner.cinepulse.data.remote.apis.postsApiService
import com.partner.cinepulse.data.remote.models.ExploreActivityResponse
import com.partner.cinepulse.data.remote.models.PostCreateRequest
import com.partner.cinepulse.data.remote.models.PostResponse
import com.partner.cinepulse.data.repository.PostRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postsApiService: postsApiService
) : PostRepository {

    override suspend fun getExploreFeed(skip: Int, limit: Int): Flow<Resource<List<ExploreActivityResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = postsApiService.getExploreFeed(skip, limit)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching explore feed failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun createPost(request: PostCreateRequest): Flow<Resource<PostResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = postsApiService.createPost(request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Post creation failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun getUserPosts(userId: Int, skip: Int, limit: Int): Flow<Resource<List<PostResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = postsApiService.getUserPosts(userId, skip, limit)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching user posts failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun getUserReviews(userId: Int): Flow<Resource<List<ExploreActivityResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = postsApiService.getUserReviews(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching user reviews failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }
}
