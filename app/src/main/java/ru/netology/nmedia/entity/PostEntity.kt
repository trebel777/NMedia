package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val video: String,
    val likes: Long = 0,
    val replys: Long = 0,
    val replyByMe: Boolean = false,
    val likedByMe: Boolean = false
) {
    fun toDto() = Post(id, author,authorAvatar, content, published, video, likes, replys, replyByMe, likedByMe)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id!!, dto.author,dto.authorAvatar, dto.content!!, dto.published!!, dto.video!!, dto.likes!!, dto.replys!!, dto.replyByMe!!,dto.likedByMe!!)

    }
}