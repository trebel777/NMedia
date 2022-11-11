package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    var ps = PostService()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        viewModel.data.observe(
            this
        ) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likeCount.text = ps.getFormatedNumber(post.likes)
                replyCount.text = ps.getFormatedNumber(post.replys)
                like.setImageResource(
                    if (post.likedByMe) R.drawable.liked_24 else R.drawable.like
                )
            }
        }
        binding.like?.setOnClickListener {
            viewModel.like()
        }
        binding.reply?.setOnClickListener {
            viewModel.reply()
        }
    }
}


