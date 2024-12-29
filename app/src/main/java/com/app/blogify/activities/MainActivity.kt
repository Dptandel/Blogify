package com.app.blogify.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blogify.adapters.BlogAdapter
import com.app.blogify.databinding.ActivityMainBinding
import com.app.blogify.models.Blog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var databaseRef: DatabaseReference
    private val blogs = mutableListOf<Blog>()
    private lateinit var auth: FirebaseAuth
    private lateinit var blogAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("blogs")

        // Set up RecyclerView and Adapter
        blogAdapter = BlogAdapter(this, blogs)
        binding.rvBlogs.apply {
            adapter = blogAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Load user profile image
        auth.currentUser?.uid?.let { loadUserProfileImage(it) }

        // Fetch and display blogs
        fetchBlogs()

        // Set up listeners
        binding.fabAddBlog.setOnClickListener {
            startActivity(Intent(this, AddBlogActivity::class.java))
        }

        binding.cvProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.ibSavedArticles.setOnClickListener {
            startActivity(Intent(this, SavedBlogsActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchBlogs() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogs.clear()
                for (childSnapshot in snapshot.children) {
                    val blog = childSnapshot.getValue(Blog::class.java)
                    blog?.let { blogs.add(it) }
                }
                blogs.reverse() // To display the newest blogs first
                blogAdapter.notifyDataSetChanged()

                // Handle empty state
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading failed!", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun loadUserProfileImage(userId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userRef.child("profileImage").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.ivProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to load profile image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}