package com.app.blogify

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Data
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.blogify.databinding.ActivityAddBlogBinding
import com.app.blogify.models.Blog
import com.app.blogify.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddBlogActivity : AppCompatActivity() {

    private val binding: ActivityAddBlogBinding by lazy {
        ActivityAddBlogBinding.inflate(layoutInflater)
    }
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("blogs")
    private val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnAddBlog.setOnClickListener {
            val blogTitle = binding.edtTitle.text.toString().trim()
            val blogDescription = binding.edtBlog.text.toString().trim()
            addBlog(blogTitle, blogDescription)
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun addBlog(blogTitle: String, blogDescription: String) {
        if (blogTitle.isEmpty() || blogDescription.isEmpty()) {
            Toast.makeText(this, "please fill all the fields", Toast.LENGTH_SHORT).show()
        }

        // get current user
        val user: FirebaseUser? = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val username = user.displayName ?: "Unknown"
            val userImageUrl = user.photoUrl ?: ""

            // fetch username and profile from database
            userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SimpleDateFormat")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    if (userData != null) {
                        val userName = userData.name
                        val userProfile = userData.profileImage
                        val currentDate = SimpleDateFormat("MMMM dd, yyyy").format(Date())

                        // create blog
                        val blog = Blog(
                            blogTitle, userName, currentDate, blogDescription, 0, userProfile
                        )

                        // generate unique key for blog
                        val blogId = databaseRef.push().key
                        if (blogId != null) {
                            blog.blogId = blogId
                            val blogReference = databaseRef.child(blogId)
                            blogReference.setValue(blog).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this@AddBlogActivity,
                                        "Blog posted successfully!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@AddBlogActivity,
                                        "Failed to add blog",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}