package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.TimeSeparatorValue


sealed class FeedItem{
    abstract val id: Long
}

data class TimeSeparator(
    override val id: Long,
    val value: TimeSeparatorValue,
) : FeedItem()
data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Long = 0,
    var attachment: Attachment? = null,
    val ownedByMe: Boolean = false
): FeedItem() {
    var replys: Long = 0
    var replyByMe: Boolean = false
    val video: String = ""
}

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType,
)
