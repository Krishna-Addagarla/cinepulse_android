package com.partner.cinepulse.data.remote.models

import com.google.type.DateTime

data class searchResponse(
    val results : List<searchItem>,
    val total : Int,
    val query : String
)

data class searchItem(
    val id : Int,
    val name : String,
    val photo_url : String,
    val type : String,
    val subtitle : String,
    val rating : Double,
    val match_score : Double
)

data class actorResponse(
    val name: String,
    val photo_url: String,
    val biography: String,
    val birth_date: String,
    val birth_place: String,
    val occupation: String,
    val id : Int,
    val overall_rating : Int,
    val total_ratings : Int,
    val filmography: List<String>,
    val awards: List<String>
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

data class movieResponse(
    val name: String,
    val photo_url: String,
    val plot: String,
    val release_date: String,
    val release_year: Int,
    val runtime_minutes: Int,
    val id : Int,
    val overall_rating : Int,
    val total_ratings : Int,
    val genres: List<String>,
    val cast: List<String>,
    val crew: List<String>,
    val awards:List<String>
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
    val person_id: Int,
    val person_name: String,
    val person_type: String ,
    val role: String ?= null,
    val rating: Double
)

data class reviewResponse(
    val rating : Double,
    val review_text: String,
    val id: Int,
    val user_id: Int,
    val user_name: String,
    val created_at: DateTime,
    val performance_ratings: List<performanceRating>
)

