package com.app.blogify

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blogify.adapters.BlogAdapter
import com.app.blogify.databinding.ActivitySavedBlogsBinding
import com.app.blogify.models.Blog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedBlogsActivity : AppCompatActivity() {

    private val binding: ActivitySavedBlogsBinding by lazy {
        ActivitySavedBlogsBinding.inflate(layoutInflater)
    }

    private val savedBlogs = mutableListOf<Blog>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val savedBlogsAdapter = BlogAdapter(this, savedBlogs.filter { it.isSaved }.toMutableList())
        binding.rvBlogs.layoutManager = LinearLayoutManager(this)
        binding.rvBlogs.adapter = savedBlogsAdapter

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val databaseRef =
                FirebaseDatabase.getInstance().getReference("users").child(userId).child("saved")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (blogSnapshot in snapshot.children) {
                        val blogId = blogSnapshot.key
                        val isSaved = blogSnapshot.value as Boolean
                        if (blogId != null && isSaved) {
                            // fetch the corresponding blog with blogId using coroutine
                            CoroutineScope(Dispatchers.IO).launch {
                                val blog = fetchBlog(blogId)
                                if (blog != null) {
                                    savedBlogs.add(blog)
                                    launch(Dispatchers.Main) {
                                        savedBlogsAdapter.updateData(savedBlogs)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@SavedBlogsActivity,
                        "Failed to load saved blogs",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private suspend fun fetchBlog(blogId: String): Blog? {
        val blogRef = FirebaseDatabase.getInstance().getReference("blogs")
        return try {
            val dataSnapshot = blogRef.child(blogId).get().await()
            val blog = dataSnapshot.getValue(Blog::class.java)
            blog
        } catch (e: Exception) {
            null
        }
    }
}