package com.osmandroid.movieflix.data.api

import com.osmandroid.movieflix.data.model.NowPlayingMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String
    ): NowPlayingMoviesResponse
}