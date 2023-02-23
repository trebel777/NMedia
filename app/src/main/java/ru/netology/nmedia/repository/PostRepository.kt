package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callback: Callback<List<Post>>)
    fun likeById(post: Post, callback: Callback<Post>)
    fun replyById(id: Long?, callback: Callback<Post>)
    fun removeById(id: Long?, callback: Callback<Unit>)
    fun save(post: Post, callback: Callback<Unit>)
    fun getPost(id: Long?, callback: Callback<Post>)


    interface Callback<T> {
        fun onSuccess (data: T)
        fun onError (e: Exception)
    }
}
