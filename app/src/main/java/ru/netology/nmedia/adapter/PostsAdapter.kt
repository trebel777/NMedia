package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PostService
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

typealias OnLikeListener = (post: Post) -> Unit
typealias OnReplyListener = (post: Post) -> Unit
var ps = PostService()

class PostsAdapter(private val onLikeListener: OnLikeListener,private val onReplyListener: OnReplyListener) : RecyclerView.Adapter<PostViewHolder>() {

    var list = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onReplyListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = list.size
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onReplyListener: OnReplyListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = ps.getFormatedNumber(post.likes)
            replyCount.text = ps.getFormatedNumber(post.replys)
            like.setImageResource(
                if (post.likedByMe) R.drawable.liked_24 else R.drawable.like
            )
            if (post.likedByMe) {
                like.setImageResource(R.drawable.liked_24)
            }
            like.setOnClickListener{
                onLikeListener(post)
            }
            reply.setOnClickListener {
                onReplyListener(post)
            }
        }
    }
}