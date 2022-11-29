package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

class PostViewModel : ViewModel() {
    val edited = MutableLiveData(empty)
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun save() {
        edited.value?.let{
            repository.save(it)
        }
        edited.value = empty
    }
    fun edit(post: Post) {
        edited.value = post
    }
    fun changeContent(content: String){
        val text = content.trim()
        if(edited.value?.content == text){
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
    fun cancelEdit(){
        edited.value = empty
    }
    fun likeById(id: Long) = repository.likeById(id)
    fun replyById(id: Long) = repository.replyById(id)
    fun removeById(id: Long) = repository.removeById(id)
}

