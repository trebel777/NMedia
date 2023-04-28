package ru.netology.nmedia.viewmodel


import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.TimeSeparator
import ru.netology.nmedia.enumeration.TimeSeparatorValue
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.random.Random


private val empty = Post(
    id = 0,
    authorId = 0L,
    content = "",
    author = "",
    authorAvatar = "",
    likes = 0,
    likedByMe = false,
    published = 0

)
private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth,
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val cached: Flow<PagingData<FeedItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = {before, after ->
                    if (after == null){
                        null
                    }else if (before == null){
                        TimeSeparator(Random.nextLong(), getTimeSeparatorValue(after))
                    }else{
                        if (getTimeSeparatorValue(before) != getTimeSeparatorValue(after)){
                            TimeSeparator(Random.nextLong(), getTimeSeparatorValue(after))
                        }else{
                            null
                        }
                    }
                }
            )
        }
        .cachedIn(viewModelScope)

    @RequiresApi(Build.VERSION_CODES.O)
    val data: Flow<PagingData<FeedItem>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached
                .map { pagingData ->
                    pagingData.map { item ->
                        if (item !is Post) item else item.copy(ownedByMe = item.authorId == myId)
                    }

                }
        }


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo


    init {
        loadPosts()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeSeparatorValue(post: Post): TimeSeparatorValue {
        val timeNow = OffsetDateTime.now().toEpochSecond()
        val postTime = post.published.toLong()
        val result = when {
            postTime > (timeNow - 24 * 60 * 60) -> TimeSeparatorValue.TODAY
            postTime <= (timeNow - 24 * 60 * 60) && postTime >= (timeNow - 48 * 60 * 60) -> TimeSeparatorValue.YESTERDAY
            else -> TimeSeparatorValue.LAST_WEEK
        }
        return result
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            //repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            //repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.uri?.let { uri ->
                            repository.saveWithAttachment(it, MediaUpload(uri.toFile()))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }


    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
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

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.likeById(post)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun replyById(id: Long?) {
        TODO()

    }


    fun removeById(post: Post) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.removeById(post)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun readNewPosts() = viewModelScope.launch {
        repository.readNewPosts()
    }

    fun getPost(id: Long?) {
        TODO()
    }

}


