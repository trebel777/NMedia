package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

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
    private val repository: PostRepository = PostRepositoryImpl(
    )
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
    get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
                loadPosts()
            }
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


    fun likeById(id: Long) {
        thread {
        val post = data.value?.posts?.find { it.id == id } ?: empty
                val likedPost = repository.likeById(post)
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map { if (it.id == id) likedPost else it }
                    )
                )
        }
    }

    fun replyById(id: Long?) {
        thread {
            repository.replyById(id)
            loadPosts()
        }
    }
    fun removeById(id: Long?) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            ))
            try {
                repository.removeById(id)
            }catch (e: IOException){
                _data.postValue(_data.value?.copy(posts = old))
            }
            loadPosts()
        }
    }
    fun getPost(id: Long?) {
        thread {
            repository.getPost(id)
        }
    }
}


