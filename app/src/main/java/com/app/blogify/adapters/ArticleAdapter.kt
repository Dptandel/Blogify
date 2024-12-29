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
    private var articles: List<Blog>,
    private val onItemClick: OnItemClickListener
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

            btnEdit.setOnClickListener {
                onItemClick.onEditClick(article)
            }
            btnDelete.setOnClickListener {
                onItemClick.onDeleteClick(article)
            }
            btnReadMore.setOnClickListener {
                onItemClick.onReadMoreClick(article)
            }
        }
    }

    fun setData(articles: ArrayList<Blog>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onEditClick(article: Blog)
        fun onDeleteClick(article: Blog)
        fun onReadMoreClick(article: Blog)
    }
}