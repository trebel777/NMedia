package ru.netology.nmedia.repository


import android.app.Application
import retrofit2.Response
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import kotlin.Exception

class PostRepositoryImpl(private val application: Application) : PostRepository {

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(call: retrofit2.Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(errorDescription(response)))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }

    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
        if (!post.likedByMe!!) {
            PostsApi.retrofitService.likeById(post.id!!).enqueue(object : retrofit2.Callback<Post>{

                override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(errorDescription(response)))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(t as Exception)
                }
            })
        } else {
            PostsApi.retrofitService.dislikeById(post.id!!).enqueue(object : retrofit2.Callback<Post>{

                override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(errorDescription(response)))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(t as Exception)
                }
            })
        }
    }

    override fun replyById(id: Long?, callback: PostRepository.Callback<Post>) {
        TODO("Not yet implemented")
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post>{

            override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(errorDescription(response)))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }

    override fun getPost(id: Long?, callback: PostRepository.Callback<Post>) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long?, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id!!).enqueue(object : retrofit2.Callback<Unit>{
            override fun onResponse(call: retrofit2.Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(errorDescription(response)))
                    return
                }

                callback.onSuccess(Unit)
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback.onError(t as Exception)
            }
        })
    }
    fun <T : Any?> errorDescription(response: Response<T>):
            String = application.getString(
        R.string.error_text,
        response.code().toString(),
        response.message())
}
