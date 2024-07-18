package com.manoj.baseproject.data.bean

data class Post(
    val id: String,
    val image: String,
    val likes: Int,
    val owner: Owner,
    val publishDate: String,
    val tags: List<String>,
    val text: String
)
data class Owner(
    val firstName: String,
    val id: String,
    val lastName: String,
    val picture: String,
    val title: String
)

data class Posts(val data:List<Post>)