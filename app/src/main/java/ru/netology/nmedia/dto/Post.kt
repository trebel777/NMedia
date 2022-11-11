package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 11600,
    var replys: Int = 999_000,
    var replyByMe: Boolean = false,
    var likedByMe: Boolean = false
)
