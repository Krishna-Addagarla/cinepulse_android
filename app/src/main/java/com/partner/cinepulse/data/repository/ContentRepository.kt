package com.partner.cinepulse.data.repository

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
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ContentRepository {

    suspend fun getMoviesInTheaters() : Flow<Resource<List<movieResponse>>>
    suspend fun getTrendingArtists() : Flow<Resource<List<ArtistResponse>>>
    suspend fun searchContent(q:String) : Flow<Resource<searchResponse>>
    suspend fun getActor(actor_id: Int) : Flow<Resource<actorResponse>>

    suspend fun getCrew(crew_id : Int) : Flow<Resource<crewResponse>>
    suspend fun getMovie(movieId : Int) : Flow<Resource<movieResponse>>
    suspend fun getTvShow(tvShowId : Int) : Flow<Resource<tvshowResponse>>
    suspend fun postReview(movieId : Int,request: reviewRequest) : Flow<Resource<reviewResponse>>
    suspend fun getMovieReview(movieId : Int,skip : Int,limit : Int) : Flow<Resource<List<Review>>>

    suspend fun addFavorite(request: FavoriteAddRequest): Flow<Resource<FavoriteResponse>>
    suspend fun removeFavorite(movieId: Int?, tvShowId: Int?): Flow<Resource<Map<String, String>>>
    suspend fun listFavorites(): Flow<Resource<List<FavoriteResponse>>>
    suspend fun checkFavoriteStatus(movieId: Int?, tvShowId: Int?): Flow<Resource<FavoriteStatusResponse>>
    suspend fun listCollections(): Flow<Resource<List<CollectionResponse>>>
    suspend fun createCollection(request: CollectionCreateRequest): Flow<Resource<CollectionResponse>>
    suspend fun renameCollection(collectionId: Int, request: CollectionRenameRequest): Flow<Resource<CollectionResponse>>
    suspend fun deleteCollection(collectionId: Int): Flow<Resource<Map<String, String>>>
    suspend fun addCollectionItem(collectionId: Int, request: CollectionItemAddRequest): Flow<Resource<CollectionItemResponse>>
    suspend fun removeCollectionItem(collectionId: Int, movieId: Int?, tvShowId: Int?): Flow<Resource<Map<String, String>>>
    suspend fun viewCollection(collectionId: Int): Flow<Resource<CollectionResponse>>

    suspend fun getTrendingSearches(region: String?): Flow<Resource<List<com.partner.cinepulse.data.remote.models.searchItem>>>
    suspend fun getSearchSuggestions(recentQueries: List<String>, recentViews: List<String>): Flow<Resource<List<com.partner.cinepulse.data.remote.models.searchItem>>>
}