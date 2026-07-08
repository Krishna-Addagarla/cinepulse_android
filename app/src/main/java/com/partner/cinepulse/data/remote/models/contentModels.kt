package com.partner.cinepulse.data.remote.models

data class searchResponse(
    val results : List<searchItem>,
    val total : Int,
    val query : String
)

data class searchItem(
    val id : Int,
    val name : String,
    val photo_url : String?,
    val type : String,
    val subtitle : String?,
    val rating : Double=0.0,
    val match_score : Double = 0.0
)

data class actorResponse(
    val name: String,
    val photo_url: String,
    val biography: String,
    val birth_date: String,
    val birth_place: String,
    val occupation: String,
    val id : Int,
    val overall_rating : Float,
    val total_ratings : Int,
    val filmography: List<Filmography>,
    val awards: List<String>
)

data class  Filmography(
    val id: Int,
    val title: String,
    val release_year: Int,
    val character_name: String,
    val rating: Float,
    val total_ratings: Int,
    val poster_url: String,
    val media_type : String
)

data class crewResponse(
    val name: String,
    val photo_url: String,
    val biography: String,
    val birth_date: String,
    val birth_place: String,
    val occupation: String,
    val id : Int,
    val overall_rating : Int,
    val total_ratings : Int,
)

data class CastMember(
    val id: Int,
    val name: String,
    val character_name: String,
    val photo_url: String,
    val rating: Float,
    val total_ratings: Int
)

data class movieResponse(
    val title: String,
    val photo_url: String,
    val plot: String,
    val release_date: String,
    val release_year: Int,
    val runtime_minutes: Int,
    val id: Int,
    val overall_rating: Double,
    val total_ratings: Int,
    val genres: List<Genres>,
    val credits: List<Credit>,
    val awards: List<Any> // Using Any since awards array appears empty, can be replaced with specific type if needed
)

data class Genres(
    val name: String,
    val id: Int
)

data class Credit(
    val id: Int,
    val name: String,
    val photo_url: String,
    val role: Role,
    val character_name: String,
    val job_detail: Any? = null, // Nullable since it's null in the example
    val rating: Double,
    val total_ratings: Int
)

data class Role(
    val name: String,
    val id: Int
)

data class tvshowResponse(
    val name: String,
    val photo_url: String,
    val plot: String,
    val first_air_date : String,
    val last_air_date: String,
    val release_year: Int,
    val number_of_seasons: Int,
    val number_of_episodes: Int,
    val runtime_minutes: Int,
    val id : Int,
    val overall_rating : Int,
    val total_ratings : Int,
    val genres: List<String>,
    val cast: List<String>,
    val crew: List<String>,
    val awards:List<String>
)

data class reviewRequest(
    val rating : Double,
    val review_text: String,
    val performance_ratings: List<performanceRating>
)

data class performanceRating(
    val artist_id   : Int,
    val role_id     : Int,
    val person_name : String,
    val person_type : String,
    val rating      : Double
)

data class reviewResponse(
    val rating : Double,
    val review_text: String,
    val id: Int,
    val user_id: Int,
    val user_name: String,
    val created_at: String,
    val performance_ratings: List<performanceRating>
)

data class Review(
    val rating: Int,
    val review_text: String,
    val id: Int,
    val user_id: Int,
    val user_name: String,
    val created_at: String,
    val performance_ratings: List<ArtistsPerformanceRatings>
)

data class ArtistsPerformanceRatings(
    val artistId: Int,
    val artistName: String,
    val role: Role,
    val rating: Int
)

data class ArtistResponse(
    val id: Int,
    val name: String,
    val photo_url: String?,
    val overall_rating: Float,
    val total_ratings: Int,
    val roles: List<Role>,
    val created_at: String
)

data class ExploreActivityResponse(
    val activity_type: String, // "review" | "discussion" | "post"
    val id: Int,
    val user_id: Int,
    val user_name: String,
    val user_photo: String?,
    val time_ago: String,
    val created_at: String,
    
    // For reviews
    val rating: Float?,
    val movie_title: String?,
    val movie_id: Int?,
    val tvshow_title: String?,
    val tvshow_id: Int?,
    val review_text: String?,
    
    // For posts & discussions
    val content: String?,
    val media_urls: List<String>?,
    val fanclub_id: Int?,
    val fanclub_name: String?,
    val likes_count: Int?,
    val comments_count: Int?,
    @com.google.gson.annotations.SerializedName("is_liked_by_current_user")
    val is_liked_by_current_user: Boolean?,
    
    // Tags
    val tagged_artists: List<ArtistTag>?,
    val tagged_movies: List<MovieTag>?,
    val tagged_tvshows: List<TVShowTag>?
)

data class ArtistTag(
    val id: Int,
    val name: String,
    val photo_url: String?
)

data class MovieTag(
    val id: Int,
    val title: String,
    val poster_url: String?,
    val release_year: Int?
)

data class TVShowTag(
    val id: Int,
    val title: String,
    val poster_url: String?,
    val release_year: Int?
)

data class PostResponse(
    val id: Int,
    val content: String,
    val media_urls: List<String>,
    @com.google.gson.annotations.SerializedName("is_public")
    val is_public: Boolean,
    val user_id: Int,
    val user_name: String,
    val user_photo: String?,
    val fanclub_id: Int?,
    val fanclub_name: String?,
    val created_at: String,
    val updated_at: String?,
    val likes_count: Int,
    val comments_count: Int,
    @com.google.gson.annotations.SerializedName("is_liked_by_current_user")
    val is_liked_by_current_user: Boolean,
    val tagged_artists: List<ArtistTag>,
    val tagged_movies: List<MovieTag>,
    val tagged_tvshows: List<TVShowTag>
)

data class PostCreateRequest(
    val content: String,
    val media_urls: List<String> = emptyList(),
    @com.google.gson.annotations.SerializedName("is_public")
    val is_public: Boolean = true,
    val fanclub_id: Int? = null,
    val tagged_artist_ids: List<Int> = emptyList(),
    val tagged_movie_ids: List<Int> = emptyList(),
    val tagged_tvshow_ids: List<Int> = emptyList()
)

data class FavoriteAddRequest(
    val movie_id: Int? = null,
    val tv_show_id: Int? = null
)

data class FavoriteResponse(
    val id: Int,
    val user_id: Int,
    val movie_id: Int?,
    val tv_show_id: Int?,
    val created_at: String,
    val content_type: String, // "movie" | "tv_show"
    val movie: movieResponse?,
    val tv_show: tvshowResponse?
)

data class FavoriteStatusResponse(
    val is_favorite: Boolean
)

data class CollectionCreateRequest(
    val name: String
)

data class CollectionRenameRequest(
    val name: String
)

data class CollectionItemAddRequest(
    val movie_id: Int? = null,
    val tv_show_id: Int? = null
)

data class CollectionItemResponse(
    val id: Int,
    val collection_id: Int,
    val movie_id: Int?,
    val tv_show_id: Int?,
    val created_at: String,
    val content_type: String, // "movie" | "tv_show"
    val movie: movieResponse?,
    val tv_show: tvshowResponse?
)

data class CollectionResponse(
    val id: Int,
    val user_id: Int,
    val name: String,
    val is_watchlist: Boolean,
    val created_at: String,
    val item_count: Int,
    val items: List<CollectionItemResponse> = emptyList()
)


