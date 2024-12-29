package com.app.blogify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blogify.adapters.ArticleAdapter
import com.app.blogify.databinding.ActivityArticlesBinding
import com.app.blogify.models.Blog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticlesActivity : AppCompatActivity() {

    private val binding: ActivityArticlesBinding by lazy {
        ActivityArticlesBinding.inflate(layoutInflater)
    }
    private lateinit var databaseRef: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private lateinit var articlesAdapter: ArticleAdapter

    private val EDIT_BLOG_REQ_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener {
            finish()
        }

        val currentUserId = auth.currentUser?.uid

        binding.rvArticles.layoutManager = LinearLayoutManager(this)

        if (currentUserId != null) {

            articlesAdapter =
                ArticleAdapter(this, emptyList(), object : ArticleAdapter.OnItemClickListener {
                    override fun onEditClick(article: Blog) {
                        editArticle(article)
                    }

                    override fun onDeleteClick(article: Blog) {
                        deleteArticle(article)
                    }

                    override fun onReadMoreClick(article: Blog) {
                        readArticle(article)
                    }

                })
        }
        binding.rvArticles.adapter = articlesAdapter

        // get articles
        databaseRef = FirebaseDatabase.getInstance().getReference("blogs")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val articles = ArrayList<Blog>()
                for (articleSnapshot in snapshot.children) {
                    val articleSaved = articleSnapshot.getValue(Blog::class.java)
                    if (articleSaved != null && currentUserId == articleSaved.userId) {
                        articles.add(articleSaved)
                    }
                }
                articlesAdapter.setData(articles)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ArticlesActivity, "Error Loading Articles!!", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun editArticle(article: Blog) {
        val intent = Intent(this@ArticlesActivity, EditBlogActivity::class.java)
        intent.putExtra("blog", article)
        startActivityForResult(intent, EDIT_BLOG_REQ_CODE)
    }

    private fun deleteArticle(article: Blog) {
        val articleId = article.blogId
        val blogRef = databaseRef.child(articleId!!)

        blogRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Article Deleted Successfully!!!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Article Not Deleted!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readArticle(article: Blog) {
        val intent = Intent(this@ArticlesActivity, BlogActivity::class.java)
        intent.putExtra("blog", article)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_BLOG_REQ_CODE && resultCode == RESULT_OK){}
    }
}