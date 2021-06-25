package com.osmandroid.movieflix.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.osmandroid.movieflix.data.api.MovieApi
import com.osmandroid.movieflix.data.repository.MovieRepository
import com.osmandroid.movieflix.ui.main.viewmodel.MovieViewModel

class ViewModelFactory(private val movieApi: MovieApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java))
            return MovieViewModel(MovieRepository(movieApi)) as T

        throw IllegalArgumentException("Unknown class name")
    }

}