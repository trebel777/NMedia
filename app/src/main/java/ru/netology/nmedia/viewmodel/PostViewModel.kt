package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likes = 0,
    replys = 0,
    likedByMe = false,
    replyByMe = false,
    published = ""

)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    val edited = MutableLiveData(empty)
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )
    val data = repository.getAll()
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun cancelEdit() {
        edited.value = empty
    }


    fun likeById(id: Long?) = repository.likeById(id)
    fun replyById(id: Long?) = repository.replyById(id)
    fun removeById(id: Long?) = repository.removeById(id)
    fun getPost(id: Long?) = repository.getPost(id)
}


