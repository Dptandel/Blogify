package com.app.blogify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.blogify.activities.BlogActivity
import com.app.blogify.R
import com.app.blogify.databinding.BlogItemBinding
import com.app.blogify.models.Blog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BlogAdapter(
    private val context: Context,
    private val blogs: MutableList<Blog>
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    inner class BlogViewHolder(var binding: BlogItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = BlogItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int = blogs.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.binding.apply {
            tvTitle.text = blog.title
            tvUsername.text = blog.username
            tvDate.text = blog.date
            tvBlog.text = blog.blog
            tvLikes.text = blog.likes.toString()

            Glide.with(context)
                .load(blog.profile)
                .into(ivProfile)

            btnReadMore.setOnClickListener {
                val intent = Intent(context, BlogActivity::class.java)
                intent.putExtra("blog", blog)
                context.startActivity(intent)
            }

            blogItem.setOnClickListener {
                val intent = Intent(context, BlogActivity::class.java)
                intent.putExtra("blog", blog)
                context.startActivity(intent)
            }

            // Handle likes
            val blogId = blog.blogId
            if (blogId != null && currentUser != null) {
                val blogLikeRef = databaseRef.child("blogs").child(blogId).child("likedBy")
                blogLikeRef.child(currentUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            ibLike.setImageResource(
                                if (snapshot.exists()) R.drawable.ic_liked else R.drawable.ic_like_black
                            )
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("BlogAdapter", "Error fetching like state: $error")
                        }
                    })

                ibLike.setOnClickListener {
                    handleLikes(blogId, this, position)
                }
            }

            // Handle save button
            val userRef = databaseRef.child("users").child(currentUser?.uid ?: "")
            val savedBlogsRef = userRef.child("saved").child(blogId!!)

            savedBlogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ibSave.setImageResource(
                        if (snapshot.exists()) R.drawable.ic_saved else R.drawable.ic_save_red
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BlogAdapter", "Error fetching save state: $error")
                }
            })

            ibSave.setOnClickListener {
                if (currentUser != null) {
                    handleSaves(blogId, this, position)
                } else {
                    Toast.makeText(context, "Login Required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLikes(blogId: String, binding: BlogItemBinding, position: Int) {
        val blogLikeRef = databaseRef.child("blogs").child(blogId).child("likedBy")
        val likeCountRef = databaseRef.child("blogs").child(blogId).child("likes")
        val userRef = databaseRef.child("users").child(currentUser!!.uid).child("likes")

        blogLikeRef.child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Unlike logic
                        blogLikeRef.child(currentUser.uid).removeValue()
                        userRef.child(blogId).removeValue()
                        updateLikeCount(likeCountRef, -1, position)
                    } else {
                        // Like logic
                        blogLikeRef.child(currentUser.uid).setValue(true)
                        userRef.child(blogId).setValue(true)
                        updateLikeCount(likeCountRef, 1, position)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BlogAdapter", "Failed to handle likes: $error")
                }
            })
    }

    private fun updateLikeCount(likeCountRef: DatabaseReference, delta: Int, position: Int) {
        likeCountRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentCount = mutableData.getValue(Int::class.java) ?: 0
                mutableData.value = currentCount + delta
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {
                if (committed) {
                    val newLikeCount = snapshot?.getValue(Int::class.java) ?: 0
                    blogs[position].likes = newLikeCount
                    notifyItemChanged(position)
                } else {
                    Log.e("BlogAdapter", "Error updating like count: $error")
                }
            }
        })
    }

    private fun handleSaves(blogId: String, binding: BlogItemBinding, position: Int) {
        val userRef = databaseRef.child("users").child(currentUser!!.uid)
        userRef.child("saved").child(blogId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userRef.child("saved").child(blogId).removeValue().addOnSuccessListener {
                            blogs[position].isSaved = false
                            notifyItemChanged(position)
                            Toast.makeText(context, "Blog Unsaved!!!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        userRef.child("saved").child(blogId).setValue(true).addOnSuccessListener {
                            blogs[position].isSaved = true
                            notifyItemChanged(position)
                            Toast.makeText(context, "Blog Saved!!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BlogAdapter", "Failed to handle saves: $error")
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newBlogs: MutableList<Blog>) {
        blogs.clear()
        blogs.addAll(newBlogs)
        notifyDataSetChanged()
    }
}