package ru.netology.nmedia.repository


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException
import ru.netology.nmedia.error.UnknownError


class PostRepositoryImpl(private val dao: PostDao, private val application: Application): PostRepository {
    override val data: LiveData<List<Post>> = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) : Post {
        try {
            likeByIdLocal(post.id!!)
            val response = if (!post.likedByMe!!) {
                PostsApi.retrofitService.likeById(post.id)
            } else{
                PostsApi.retrofitService.dislikeById(post.id)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return body
        } catch (e: IOException) {
            likeByIdLocal(post.id!!)
            throw NetworkError
        } catch (e: Exception) {
            likeByIdLocal(post.id!!)
            throw UnknownError
        }
    }


    override suspend fun replyById(post: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(post: Post) {
        val postRemoved = post.copy()
        try {
            dao.removeById(post.id!!)
            val response = PostsApi.retrofitService.removeById(post.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            saveLocal(postRemoved)
            throw NetworkError
        } catch (e: Exception) {
            saveLocal(postRemoved)
            throw UnknownError
        }
    }


    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getPost(id: Long): Post {
        TODO("Not yet implemented")
    }

    override suspend fun likeByIdLocal(id: Long) {
        return dao.likeById(id)
    }

    override suspend fun saveLocal(post: Post) {
        dao.insert(PostEntity.fromDto(post))
    }
}

//    fun <T : Any?> errorDescription(response: Response<T>):
//            String = application.getString(
//        R.string.error_text,
//        response.code().toString(),
//        response.message())
//}
