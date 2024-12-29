package com.app.blogify.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Blog(
    var title: String? = "",
    val username: String? = "",
    val date: String? = "",
    val userId: String? = "",
    var blog: String? = "",
    var likes: Int = 0,
    val profile: String? = "",
    var isSaved: Boolean = false,
    var blogId: String? = "",
    val likedBy: Map<String, Boolean>? = null
) : Parcelable
