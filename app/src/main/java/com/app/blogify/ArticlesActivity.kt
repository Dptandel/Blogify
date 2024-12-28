package com.app.blogify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.blogify.adapters.ArticleAdapter
import com.app.blogify.databinding.ActivityArticlesBinding

class ArticlesActivity : AppCompatActivity() {

    private val binding: ActivityArticlesBinding by lazy {
        ActivityArticlesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}