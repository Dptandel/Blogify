package com.app.blogify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blogify.databinding.YourBlogItemBinding
import com.app.blogify.models.Blog
import com.bumptech.glide.Glide

class ArticleAdapter(
    private val context: Context,
    private val articles: MutableList<Blog>
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(var binding: YourBlogItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = YourBlogItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.apply {
            tvTitle.text = article.title
            tvUsername.text = article.username
            tvDate.text = article.date
            tvBlog.text = article.blog
            Glide.with(context)
                .load(article.profile)
                .into(ivProfile)
        }
    }
}