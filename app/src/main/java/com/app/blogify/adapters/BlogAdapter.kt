package com.app.blogify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blogify.databinding.BlogItemBinding
import com.app.blogify.models.Blog
import com.bumptech.glide.Glide

class BlogAdapter(
    private val context: Context,
    private val blogs: List<Blog>
) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
    inner class BlogViewHolder(var binding: BlogItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = BlogItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int = blogs.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.binding.tvTitle.text = blog.title
        holder.binding.tvUsername.text = blog.username
        holder.binding.tvDate.text = blog.date
        holder.binding.tvBlog.text = blog.blog
        holder.binding.tvLikes.text = blog.likes.toString()
        Glide.with(context).load(blog.profile).into(holder.binding.ivProfile)
    }
}