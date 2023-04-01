package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val video: String?,
    val likes: Long = 0,
    val replys: Long = 0,
    val replyByMe: Boolean = false,
    val likedByMe: Boolean = false,
    val read: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likes, likedByMe, attachment?.toDto())
    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.video,
                dto.likes,
                dto.replys,
                dto.replyByMe,
                dto.likedByMe,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment)
            )

        fun fromDtoNew(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.video,
                dto.likes,
                dto.replys,
                dto.replyByMe,
                dto.likedByMe,
                read = false,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment)
            )

    }
}
data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, "", type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
fun List<Post>.toEntityNew(): List<PostEntity> = map(PostEntity::fromDtoNew)