package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post: Post) :Post
    fun replyById(id: Long?)
    fun removeById(id: Long?)
    fun save(post: Post)
    fun getPost(id: Long?) : Post

}
