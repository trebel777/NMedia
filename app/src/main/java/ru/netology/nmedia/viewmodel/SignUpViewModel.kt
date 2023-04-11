package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.UserAuthResult
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val apiService: ApiService): ViewModel() {
    val name: MutableLiveData<String> = MutableLiveData<String>()
    val login: MutableLiveData<String> = MutableLiveData<String>()
    val pass: MutableLiveData<String> = MutableLiveData<String>()
    val avatar: MutableLiveData<PhotoModel?> = MutableLiveData<PhotoModel?>()


    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState>
        get() = _authState

    private val _userAuthResult = SingleLiveEvent<UserAuthResult>()
    val userAuthResult: LiveData<UserAuthResult>
        get() = _userAuthResult

    fun signUp() = viewModelScope.launch {
        try {
            when (val media = avatar.value) {
                null ->  registerUser(
                name.value.toString(),
                login.value.toString().trim(),
                pass.value.toString().trim(),
            )
                else -> registerWithPhoto(
                    name.value.toString(),
                    login.value.toString().trim(),
                    pass.value.toString().trim(),
                    media)
            }
        } catch (e: Exception) {
            _userAuthResult.value = UserAuthResult(error = true)
        }
    }
    fun changeAvatar(uri: Uri?, file: File?) {
        avatar.value = PhotoModel(uri, file)
    }

    fun clearAvatar(uri: Uri?, file: File?) {
        avatar.value = PhotoModel(uri, file)
    }

    private suspend fun registerUser(name: String, login: String, pass: String) {
        try {
            val response = apiService.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            _authState.postValue(body)
            _userAuthResult.value = UserAuthResult()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun registerWithPhoto(name: String, login: String, pass: String, avatar: PhotoModel) {
        try {
            val response = apiService.registerWithPhoto(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                MultipartBody.Part.createFormData(
                    "file", avatar.file?.name, avatar.file!!.asRequestBody()))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            _authState.postValue(body)
            _userAuthResult.value = UserAuthResult()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}

