package com.osmandroid.movieflix.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.osmandroid.movieflix.R
import com.osmandroid.movieflix.data.model.Movie
import com.osmandroid.movieflix.databinding.PopularItemBinding
import com.osmandroid.movieflix.databinding.UnpopularItemBinding
import com.osmandroid.movieflix.ui.main.view.PosterActivity
import com.osmandroid.movieflix.utils.AppUtils.Companion.BACKDROP_URL
import com.osmandroid.movieflix.utils.AppUtils.Companion.POSTER_URL
import com.osmandroid.movieflix.utils.CacheManager.Companion.setImage

class MovieAdapter(
    private val context: Activity
) :
    RecyclerView.Adapter<BaseViewHolder<*>>(), Filterable {
    private var movieList: ArrayList<Movie> = ArrayList()
    private var allMovieList: ArrayList<Movie> = ArrayList()

    companion object {
        private const val TYPE_POPULAR = 0
        private const val TYPE_UNPOPULAR = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_POPULAR -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.popular_item, parent, false)
                PopularViewHolder(view)
            }
            TYPE_UNPOPULAR -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.unpopular_item, parent, false)
                UnPopularViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = movieList[position]
        when (holder) {
            is PopularViewHolder -> holder.bind(element)
            is UnPopularViewHolder -> holder.bind(element)
            else -> throw IllegalArgumentException()
        }

    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun getItemViewType(position: Int): Int {
        val voteAverage = movieList[position].vote_average

        return if (voteAverage > 7)
            TYPE_POPULAR
        else
            TYPE_UNPOPULAR
    }

    fun setMoviesData(movies: List<Movie>) {
        movieList.clear()
        movieList.addAll(movies)
        allMovieList.clear()
        allMovieList.addAll(movies)
    }


    inner class PopularViewHolder(itemView: View) : BaseViewHolder<Movie>(itemView) {
        private val binding = PopularItemBinding.bind(itemView)

        override fun bind(item: Movie) {
            setImage(context, binding.backdropView, item.backdrop_path, BACKDROP_URL)

            itemView.setOnClickListener {
                sendToFullScreen(item)
            }
        }
    }

    inner class UnPopularViewHolder(itemView: View) : BaseViewHolder<Movie>(itemView) {
        private val binding = UnpopularItemBinding.bind(itemView)

        override fun bind(item: Movie) {
            binding.titleView.text = item.title
            binding.descriptionView.text = item.overview

            setImage(context, binding.posterView, item.poster_path, POSTER_URL)

            itemView.setOnClickListener {
                sendToFullScreen(item)
            }
        }
    }

    fun sendToFullScreen(item: Movie) {
        val intent = Intent(context, PosterActivity::class.java)
        intent.putExtra("poster_path", item.poster_path)
        context.startActivity(intent)
    }

    private var mainListUndoPosition: Int = -1
    fun removeMovie(position: Int): Movie {
        val movie = movieList.removeAt(position)
        mainListUndoPosition = allMovieList.indexOf(movie)
        allMovieList.removeAt(mainListUndoPosition)
        notifyItemRemoved(position)
        return movie
    }

    fun restoreMovie(position: Int, movie: Movie) {
        movieList.add(position, movie)
        allMovieList.add(mainListUndoPosition, movie)
        notifyItemInserted(position)
    }

    override fun getFilter(): Filter {
        return movieFilter
    }

    private val movieFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Movie> = arrayListOf()
            if (constraint.isEmpty()) {
                filteredList.addAll(allMovieList)
            } else {
                val pattern = constraint.toString().lowercase().trim { it <= ' ' }
                for (movie in allMovieList) {
                    if (movie.title.lowercase().contains(pattern) or movie.overview.lowercase()
                            .contains(pattern)
                    ) {
                        filteredList.add(movie)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            movieList.clear()
            if (results.values != null) {
                movieList.addAll(results.values as Collection<Movie>)
                notifyDataSetChanged()
            }
        }
    }
}

