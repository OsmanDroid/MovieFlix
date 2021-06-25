package com.osmandroid.movieflix.ui.main.view

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.osmandroid.movieflix.R
import com.osmandroid.movieflix.data.api.RetrofitBuilder
import com.osmandroid.movieflix.data.model.Movie
import com.osmandroid.movieflix.databinding.ActivityMainBinding
import com.osmandroid.movieflix.ui.base.ViewModelFactory
import com.osmandroid.movieflix.ui.main.adapter.MovieAdapter
import com.osmandroid.movieflix.ui.main.viewmodel.MovieViewModel
import com.osmandroid.movieflix.utils.Status


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MovieAdapter
    private lateinit var viewModel: MovieViewModel

    private lateinit var searchView: SearchView
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViewModel()
        setupUI()
        setupObservers()
        setupSwipeToDelete()
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitBuilder.moviesApi)
        ).get(MovieViewModel::class.java)
    }

    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MovieAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.getNowPlayingMovies().observe(this, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { data -> setMovies(data.results) }
                    }
                    Status.ERROR -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setMovies(movies: List<Movie>) {
        adapter.apply {
            setMoviesData(movies)
            notifyDataSetChanged()
        }
    }

    private fun setupSwipeToDelete() {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                val movie = adapter.removeMovie(position)

                val snackBar: Snackbar =
                    Snackbar.make(
                        binding.constraintLayout,
                        "Movie Item Deleted.",
                        Snackbar.LENGTH_LONG
                    )
                snackBar.setActionTextColor(Color.BLUE)
                snackBar.setAction(
                    "UNDO"
                ) {
                    adapter.restoreMovie(position, movie)
                    binding.recyclerView.smoothScrollToPosition(position)
                }
                snackBar.show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu!!.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
        } else {
            super.onBackPressed()
        }

    }

}
