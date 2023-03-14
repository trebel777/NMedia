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


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likes = 0,
    replys = 0,
    likedByMe = false,
    replyByMe = false,
    published = ""

)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    val edited = MutableLiveData(empty)
    private val repository: PostRepository = PostRepositoryImpl(application)
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
        _data.value = FeedModel(loading = true)
        repository.getAll(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }
            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true, errorText = e.toString()))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                }
                override fun onError(e: Exception) {
                    _postCreated.postValue(Unit)
                    _data.postValue(FeedModel(error = true, errorText = e.toString()))
                }
            })
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
        val post = data.value?.posts?.find { it.id == id } ?: empty
        repository.likeById(post, object : PostRepository.Callback<Post>{
            override fun onSuccess(data: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map { if (it.id == id) data else it }
                )
                )
            }
            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true, errorText = e.toString()))
            }
        })
    }

    fun replyById(id: Long?) {
            TODO()

    }
    fun removeById(id: Long?) {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            ))
        repository.removeById(id, object : PostRepository.Callback<Unit>{
            override fun onSuccess(data: Unit) {
                _data.postValue(FeedModel(posts = old))
            }

            override fun onError(e: Exception) {
                loadPosts()
                _data.postValue(FeedModel(error = true, errorText = e.toString()))
            }
        })
        }

    fun getPost(id: Long?) {
            TODO()
    }
}


