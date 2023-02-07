package ru.netology.nmedia.dto

data class Post(
    val id: Long?,
    val author: String,
    val content: String,
    val published: String,
    val video: String? = "",
    var likes: Long?,
    var replys: Long?,
    var replyByMe: Boolean?,
    var likedByMe: Boolean?
)
