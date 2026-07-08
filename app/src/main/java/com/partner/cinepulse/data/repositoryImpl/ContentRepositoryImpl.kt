package com.partner.cinepulse.data.repositoryImpl

import retrofit2.HttpException
import com.partner.cinepulse.data.remote.apis.contentApiService
import com.partner.cinepulse.data.remote.models.Review
import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.remote.models.crewResponse
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.reviewResponse
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.remote.models.tvshowResponse
import com.partner.cinepulse.data.remote.models.ArtistResponse
import com.partner.cinepulse.data.remote.models.FavoriteAddRequest
import com.partner.cinepulse.data.remote.models.FavoriteResponse
import com.partner.cinepulse.data.remote.models.FavoriteStatusResponse
import com.partner.cinepulse.data.remote.models.CollectionCreateRequest
import com.partner.cinepulse.data.remote.models.CollectionRenameRequest
import com.partner.cinepulse.data.remote.models.CollectionItemAddRequest
import com.partner.cinepulse.data.remote.models.CollectionItemResponse
import com.partner.cinepulse.data.remote.models.CollectionResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.data.repository.TokenRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val contentApiService: contentApiService,
    private val tokenRepository: TokenRepository
) : ContentRepository {

    private val movieCache = ConcurrentHashMap<Int, movieResponse>()

    override suspend fun getMoviesInTheaters(): Flow<Resource<List<movieResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.getMoviesInTheaters()

            if (response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()))
            }

        }catch (e: HttpException){
            emit(Resource.Error(e.message()))
        }catch (e: IOException){
            emit(Resource.Error("Network Error : ${e.message}"))
        }catch (e: Exception){
            emit(Resource.Error("Unexpected Error : ${e.message}"))
        }
    }

    override suspend fun getTrendingArtists(): Flow<Resource<List<ArtistResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.getTrendingArtists()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Fetching trending artists failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error : ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error : ${e.message}"))
        }
    }
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

        movieCache[movieId]?.let { cachedMovie ->
            emit(Resource.Success(cachedMovie))
            return@flow
        }
        try {
            emit(Resource.Loading())
            val response = contentApiService.getMovie(movieId)
            if (response.isSuccessful && response.body() != null) {
                movieCache[movieId] = response.body()!!
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

    override suspend fun getMovieReview(
        movieId: Int,
        skip: Int,
        limit: Int
    ): Flow<Resource<List<Review>>> = flow {
        try {
            emit(Resource.Loading())

            val response = contentApiService.getMovieReviews(
                movieId = movieId,
                skip = skip,
                limit = limit
            )

            emit(Resource.Success(response.body()!!))

        }catch (e: HttpException){
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        }catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    private suspend fun getToken(): String {
        return "Bearer ${tokenRepository.getAccessToken() ?: ""}"
    }

    override suspend fun addFavorite(request: FavoriteAddRequest): Flow<Resource<FavoriteResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.addFavorite(getToken(), request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to add favorite"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun removeFavorite(movieId: Int?, tvShowId: Int?): Flow<Resource<Map<String, String>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.removeFavorite(getToken(), movieId, tvShowId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to remove favorite"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun listFavorites(): Flow<Resource<List<FavoriteResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.listFavorites(getToken())
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to list favorites"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun checkFavoriteStatus(movieId: Int?, tvShowId: Int?): Flow<Resource<FavoriteStatusResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.checkFavoriteStatus(getToken(), movieId, tvShowId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to check favorite status"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun listCollections(): Flow<Resource<List<CollectionResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.listCollections(getToken())
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to list collections"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun createCollection(request: CollectionCreateRequest): Flow<Resource<CollectionResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.createCollection(getToken(), request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to create collection"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun renameCollection(
        collectionId: Int,
        request: CollectionRenameRequest
    ): Flow<Resource<CollectionResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.renameCollection(getToken(), collectionId, request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to rename collection"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun deleteCollection(collectionId: Int): Flow<Resource<Map<String, String>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.deleteCollection(getToken(), collectionId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to delete collection"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun addCollectionItem(
        collectionId: Int,
        request: CollectionItemAddRequest
    ): Flow<Resource<CollectionItemResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.addCollectionItem(getToken(), collectionId, request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to add item to collection"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun removeCollectionItem(
        collectionId: Int,
        movieId: Int?,
        tvShowId: Int?
    ): Flow<Resource<Map<String, String>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.removeCollectionItem(getToken(), collectionId, movieId, tvShowId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to remove item from collection"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun viewCollection(collectionId: Int): Flow<Resource<CollectionResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.viewCollection(getToken(), collectionId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to fetch collection details"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun getTrendingSearches(region: String?): Flow<Resource<List<com.partner.cinepulse.data.remote.models.searchItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.getTrendingSearches(getToken(), region)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to fetch trending searches"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "HTTP Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected Error: ${e.message}"))
        }
    }

    override suspend fun getSearchSuggestions(recentQueries: List<String>, recentViews: List<String>): Flow<Resource<List<com.partner.cinepulse.data.remote.models.searchItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response = contentApiService.getSearchSuggestions(getToken(), recentQueries, recentViews)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to fetch search suggestions"))
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