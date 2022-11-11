package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post


class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = List(100) {
        Post(
            id = it.toLong(),
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Пост номер: $it Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = 999,
            replys = 990,
            likedByMe = false,
            replyByMe = false
        )
    }
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe, likes = if (it.likedByMe) {
                    it.likes - 1
                } else {
                    it.likes + 1
                }
            )
        }
        data.value = posts
    }

    override fun replyById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                replyByMe = !it.replyByMe, replys = if (it.replyByMe) {
                    it.replys - 10
                } else {
                    it.replys + 10
                }
            )
        }
        data.value = posts
    }
}

