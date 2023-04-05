package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Long = 0,
    var attachment: Attachment? = null,
    val ownedByMe: Boolean = false
) {
    var replys: Long = 0
    var replyByMe: Boolean = false
    val video: String = ""
}

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType,
)
