package com.osmandroid.movieflix.data.repository

import com.osmandroid.movieflix.BuildConfig
import com.osmandroid.movieflix.data.api.MovieApi
import com.osmandroid.movieflix.utils.AppUtils

class MovieRepository(private val moviesApi: MovieApi) {

    suspend fun getNowPlayingMovies() = moviesApi.getNowPlayingMovies(BuildConfig.API_KEY)
}