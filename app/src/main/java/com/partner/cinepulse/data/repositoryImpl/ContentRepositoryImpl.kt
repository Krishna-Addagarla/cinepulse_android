package com.partner.cinepulse.data.repositoryImpl

import retrofit2.HttpException
import com.partner.cinepulse.data.remote.apis.contentApiService
import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.remote.models.crewResponse
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.reviewResponse
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.remote.models.tvshowResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val contentApiService: contentApiService
) : ContentRepository {

    override suspend fun searchContent(q: String): Flow<Resource<searchResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = contentApiService.searchContent(queryContent = q)
            if (response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()?:"Search Query Failed"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message()))
        }catch (e: IOException){
            emit(Resource.Error("Network Error : ${e.message}"))
        }catch (e: Exception){
            emit(Resource.Error("Unexpected Error : ${e.message}"))
        }

    }

    override suspend fun getActor(actor_id: Int): Flow<Resource<actorResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.getActor(actor_id)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching Failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun getCrew(crew_id: Int): Flow<Resource<crewResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = contentApiService.getCrew(crew_id)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching Failed"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        }catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }

    }

    override suspend fun getMovie(movieId: Int): Flow<Resource<movieResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = contentApiService.getMovie(movieId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching Failed"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        }catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }

    }

    override suspend fun getTvShow(tvShowId: Int): Flow<Resource<tvshowResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = contentApiService.getTvShow(tvShowId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching Failed"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        }catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }

    }

    override suspend fun postReview(movieId : Int, request: reviewRequest): Flow<Resource<reviewResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = contentApiService.postReview(movieId,request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to post the Review"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        }catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }

    }
}