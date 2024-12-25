package com.app.blogify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.blogify.databinding.ActivitySignInAndRegisterBinding
import com.app.blogify.models.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignInAndRegisterActivity : AppCompatActivity() {

    private val binding: ActivitySignInAndRegisterBinding by lazy {
        ActivitySignInAndRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // for visibility of Fields of Login/Register
        val action = intent.getStringExtra("action")
        if (action == "login") {
            viewLoginFields()
            binding.btnLogin.setOnClickListener {
                val loginEmail = binding.etEmail.text.toString()
                val loginPassword = binding.etPassword.text.toString()
                login(loginEmail, loginPassword)
            }
        } else if (action == "register") {
            viewRegisterFields()
            binding.btnRegister.setOnClickListener {
                val registerName = binding.etRegisterName.text.toString()
                val registerEmail = binding.etRegisterEmail.text.toString()
                val registerPassword = binding.etRegisterPassword.text.toString()
                register(registerName, registerEmail, registerPassword)
            }
        }

        // Choose image
        binding.cvRegisterProfile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
    }

    private fun register(registerName: String, registerEmail: String, registerPassword: String) {
        if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        val user = auth.currentUser
                        auth.signOut()
                        user?.let {
                            // Save user data to Firebase Realtime Database
                            val userRef = database.getReference("users")
                            val userId = user.uid
                            val userData = User(registerName, registerEmail)
                            userRef.child(userId).setValue(userData)

                            // Upload image to Firebase Storage
                            val storageRef =
                                storage.reference.child("profile_images/$userId.jpg")
                            storageRef.putFile(imageUri!!)
                            Toast.makeText(
                                this,
                                "Registration successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, WelcomeActivity::class.java))
                            finish()
                        }
                    } else {
                        // Registration failed
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun login(loginEmail: String, loginPassword: String) {
        if (loginEmail.isEmpty() && loginPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful 😁", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivRegisterProfile)
        }
    }

    private fun viewRegisterFields() {
        binding.btnRegister.setBackgroundColor(getColor(R.color.blue))
        binding.btnRegister.setTextColor(getColor(R.color.white))

        binding.btnLogin.isEnabled = false
        binding.btnLogin.alpha = 0.5f
    }

    private fun viewLoginFields() {
        binding.etEmail.visibility = View.VISIBLE
        binding.etPassword.visibility = View.VISIBLE

        binding.cvRegisterProfile.visibility = View.GONE
        binding.etRegisterName.visibility = View.GONE
        binding.etRegisterEmail.visibility = View.GONE
        binding.etRegisterPassword.visibility = View.GONE

        binding.btnLogin.setBackgroundColor(getColor(R.color.red))
        binding.btnLogin.setTextColor(getColor(R.color.white))

        binding.btnRegister.isEnabled = false
        binding.btnRegister.alpha = 0.5f
        binding.tvNewHere.alpha = 0.5f
    }
}