package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun likeById(post: Post): Post
    suspend fun save(post: Post)
    suspend fun removeById(post: Post)
    suspend fun replyById(post: Long)
    suspend fun getPost(id: Long): Post
    suspend fun likeByIdLocal(id: Long)
    suspend fun saveLocal(post: Post)
}
