package ru.netology.nmedia.dto

data class Post(
    val id: Long?,
    val author: String,
    val content: String,
    val published: String,
    val video: String? = "",
    var likes: Long? = 11600,
    var replys: Long? = 999_000,
    var replyByMe: Boolean? = false,
    var likedByMe: Boolean? = false
)
