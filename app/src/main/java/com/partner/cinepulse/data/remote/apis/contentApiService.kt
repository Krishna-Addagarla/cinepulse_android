package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.Review
import com.partner.cinepulse.data.remote.models.actorResponse
import com.partner.cinepulse.data.remote.models.crewResponse
import com.partner.cinepulse.data.remote.models.movieResponse
import com.partner.cinepulse.data.remote.models.reviewRequest
import com.partner.cinepulse.data.remote.models.reviewResponse
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.remote.models.searchItem
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

interface contentApiService {


    @GET("content/movies")
    suspend fun getMoviesInTheaters() : Response<List<movieResponse>>

    @GET("content/artists/trending")
    suspend fun getTrendingArtists() : Response<List<ArtistResponse>>

    @GET("content/search")
    suspend fun searchContent(
       @Query("q") queryContent : String
    ) : Response<searchResponse>

    @GET("content/search/trending")
    suspend fun getTrendingSearches(
        @Header("Authorization") token: String,
        @Query("region") region: String? = null
    ): Response<List<searchItem>>

    @GET("content/search/suggestions")
    suspend fun getSearchSuggestions(
        @Header("Authorization") token: String,
        @Query("recent_queries") recentQueries: List<String>,
        @Query("recent_views") recentViews: List<String>
    ): Response<List<searchItem>>

    @GET("content/artists/{artist_id}")
    suspend fun getActor(
        @Path("artist_id") actorId : Int
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

    @GET("content/movies/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId :Int,
        @Query("skip") skip:Int,
        @Query("limit") limit : Int
    ) : Response<List<Review>>

    // Favorites API
    @POST("content/favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteAddRequest
    ): Response<FavoriteResponse>

    @retrofit2.http.DELETE("content/favorites")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Query("movie_id") movieId: Int? = null,
        @Query("tv_show_id") tvShowId: Int? = null
    ): Response<Map<String, String>>

    @GET("content/favorites")
    suspend fun listFavorites(
        @Header("Authorization") token: String
    ): Response<List<FavoriteResponse>>

    @GET("content/favorites/check")
    suspend fun checkFavoriteStatus(
        @Header("Authorization") token: String,
        @Query("movie_id") movieId: Int? = null,
        @Query("tv_show_id") tvShowId: Int? = null
    ): Response<FavoriteStatusResponse>

    // Collections API
    @GET("content/collections")
    suspend fun listCollections(
        @Header("Authorization") token: String
    ): Response<List<CollectionResponse>>

    @POST("content/collections")
    suspend fun createCollection(
        @Header("Authorization") token: String,
        @Body request: CollectionCreateRequest
    ): Response<CollectionResponse>

    @retrofit2.http.PUT("content/collections/{collection_id}")
    suspend fun renameCollection(
        @Header("Authorization") token: String,
        @Path("collection_id") collectionId: Int,
        @Body request: CollectionRenameRequest
    ): Response<CollectionResponse>

    @retrofit2.http.DELETE("content/collections/{collection_id}")
    suspend fun deleteCollection(
        @Header("Authorization") token: String,
        @Path("collection_id") collectionId: Int
    ): Response<Map<String, String>>

    @POST("content/collections/{collection_id}/items")
    suspend fun addCollectionItem(
        @Header("Authorization") token: String,
        @Path("collection_id") collectionId: Int,
        @Body request: CollectionItemAddRequest
    ): Response<CollectionItemResponse>

    @retrofit2.http.DELETE("content/collections/{collection_id}/items")
    suspend fun removeCollectionItem(
        @Header("Authorization") token: String,
        @Path("collection_id") collectionId: Int,
        @Query("movie_id") movieId: Int? = null,
        @Query("tv_show_id") tvShowId: Int? = null
    ): Response<Map<String, String>>

    @GET("content/collections/{collection_id}")
    suspend fun viewCollection(
        @Header("Authorization") token: String,
        @Path("collection_id") collectionId: Int
    ): Response<CollectionResponse>
}