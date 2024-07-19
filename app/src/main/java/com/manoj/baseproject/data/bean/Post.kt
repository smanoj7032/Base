package com.manoj.baseproject.data.bean

data class Post(
    val id: String,
    var image: String,
    val likes: Int,
    val owner: Owner,
    val publishDate: String,
    val tags: List<String>,
    var text: String
)
data class Owner(
    val firstName: String,
    val id: String,
    val lastName: String,
    var picture: String,
    val title: String
)

data class Posts(val data:List<Post>)