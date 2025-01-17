package com.app.blogify.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.blogify.databinding.ActivityBlogBinding
import com.app.blogify.models.Blog
import com.bumptech.glide.Glide

class BlogActivity : AppCompatActivity() {

    private val binding: ActivityBlogBinding by lazy {
        ActivityBlogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener {
            finish()
        }

        val blog = intent.getParcelableExtra<Blog>("blog")
        if (blog != null) {
            binding.tvTitle.text = blog.title
            Glide.with(this).load(blog.profile).into(binding.ivProfile)
            binding.tvUsername.text = blog.username
            binding.tvDate.text = blog.date
            binding.tvBlog.text = blog.blog
        } else {
            Toast.makeText(this, "Failed to load blog!!!", Toast.LENGTH_SHORT).show()
        }
    }
}