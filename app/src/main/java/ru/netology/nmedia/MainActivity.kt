package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var ps = PostService()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            replyByMe = false
        )
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            if (post.likedByMe) {
                like?.setImageResource(R.drawable.liked_24)
            }
            likeCount?.text = ps.getFormatedNumber(post.likes)
            replyCount?.text = ps.getFormatedNumber(post.replys)
            root.setOnClickListener {
                Log.d("stuff", "stuff")
            }

            avatar.setOnClickListener {
                Log.d("stuff", "avatar")
            }

            like?.setOnClickListener {
                post.likedByMe = !post.likedByMe
                like.setImageResource(
                    if (post.likedByMe) R.drawable.liked_24 else R.drawable.like
                )
                if (post.likedByMe) post.likes++ else post.likes--
                likeCount?.text = ps.getFormatedNumber(post.likes)
            }
            reply?.setOnClickListener {
                post.replyByMe = !post.replyByMe
                if (post.replyByMe) post.replys += 1000 else post.replys -= 1000
                replyCount?.text = ps.getFormatedNumber(post.replys)
            }
        }
    }
}