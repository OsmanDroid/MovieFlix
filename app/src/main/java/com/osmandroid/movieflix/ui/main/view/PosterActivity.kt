package com.osmandroid.movieflix.ui.main.view

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.osmandroid.movieflix.databinding.ActivityPosterBinding
import com.osmandroid.movieflix.utils.AppUtils
import com.osmandroid.movieflix.utils.CacheManager

class PosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPosterBinding
    private var posterPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        super.onCreate(savedInstanceState)
        binding = ActivityPosterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        posterPath = intent.getStringExtra("poster_path")!!
        CacheManager.setImage(this, binding.fullPosterView, posterPath, AppUtils.POSTER_URL)
    }
}