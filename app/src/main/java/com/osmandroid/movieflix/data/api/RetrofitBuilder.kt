package com.osmandroid.movieflix.data.api

import com.osmandroid.movieflix.utils.AppUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AppUtils.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

    val moviesApi: MovieApi = buildService(MovieApi::class.java)

}