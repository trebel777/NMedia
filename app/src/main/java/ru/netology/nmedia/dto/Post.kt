package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long?,
    val author: String,
    val authorAvatar: String? = null,
    val content: String?,
    val published: String?,
    val video: String? = "",
    var likes: Long?,
    var replys: Long?,
    var replyByMe: Boolean?,
    var likedByMe: Boolean?,
    val attachment: Attachment? = null
)
data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)
