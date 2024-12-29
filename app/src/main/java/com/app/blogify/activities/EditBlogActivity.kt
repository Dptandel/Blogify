package com.app.blogify.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.blogify.R
import com.app.blogify.databinding.ActivityArticlesBinding
import com.app.blogify.databinding.ActivityEditBlogBinding
import com.app.blogify.models.Blog
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {

    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val blog = intent.getParcelableExtra<Blog>("blog")
        binding.edtTitle.setText(blog?.title)
        binding.edtBlog.setText(blog?.blog)

        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.btnSaveBlog.setOnClickListener {
            val updatedTitle = binding.edtTitle.text.toString().trim()
            val updatedBlog = binding.edtBlog.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedBlog.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                blog?.title = updatedTitle
                blog?.blog = updatedBlog

                if (blog != null) {
                    updateBlog(blog)
                }
            }

        }
    }

    private fun updateBlog(blog: Blog) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("blogs")
        val blogId = blog.blogId
        databaseRef.child(blogId!!).setValue(blog)
            .addOnSuccessListener {
                Toast.makeText(
                    this@EditBlogActivity,
                    "Blog Updated Successfully!!!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@EditBlogActivity, "Blog Not Updated!!!", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}