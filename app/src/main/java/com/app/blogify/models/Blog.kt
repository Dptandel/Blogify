package com.app.blogify.models

data class Blog(
    val title: String = "",
    val username: String = "",
    val date: String = "",
    val blog: String = "",
    val likes: Int = 0,
    val profile: String = ""
)
