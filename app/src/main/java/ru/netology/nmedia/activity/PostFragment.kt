package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.EditPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class PostFragment : Fragment() {

    val viewModel: PostViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )

        val onInteractionListener = object : OnInteractionListener {
            override fun onEdit(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                post.id?.let {
                    viewModel.likeById(post)
                }
            }

            override fun onReply(post: Post) {
                viewModel.replyById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post)
            }

            override fun onVideoClick(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }

            override fun onPost(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
                viewModel.getPost(post.id)
            }
        }
        val post = viewModel.getPost(arguments?.textArg!!.toLong())

//        binding.post.apply {
//            author.text =
//            published.text = post.published
//            content.text = post.content
//            like.isChecked = post.likedByMe!!
//            reply.isChecked = post.replyByMe!!
//            like.text = getFormatedNumber(post.likes)
//            reply.text = getFormatedNumber(post.replys)
//            playGroup.visibility = if (post.video.isNullOrBlank()) View.GONE else View.VISIBLE
//            playGroup.setOnClickListener {
//                onInteractionListener.onVideoClick(post)
//            }
//            playImage.setOnClickListener{
//                onInteractionListener.onVideoClick(post)
//            }
//            prewievImage.setOnClickListener {
//                onInteractionListener.onVideoClick(post)
//            }
//            like.setOnClickListener {
//                onInteractionListener.onLike(post)
//            }
//            reply.setOnClickListener {
//                onInteractionListener.onReply(post)
//            }
//
//            menu.setOnClickListener {
//                PopupMenu(it.context, it).apply {
//                    inflate(R.menu.options_post)
//                    setOnMenuItemClickListener { item ->
//                        when (item.itemId) {
//                            R.id.remove -> {
//                                viewModel.removeById(post.id)
//                                findNavController().navigateUp()
//                                true
//                            }
//                            R.id.edit -> {
//                                viewModel.edit(post)
//                                findNavController().navigate(R.id.action_postFragment_to_newPostFragment,
//                                    Bundle().apply {
//                                        textArg = post.content
//                                    })
//                                true
//                            }
//
//                            else -> false
//                        }
//                    }
//                }.show()
//            }
//            viewModel.data.observe(viewLifecycleOwner) { posts ->
//                like.text = posts[0].likes.toString()
//                reply.text = posts[0].replys.toString()
//            }
//    }
        return binding.root
    }
}


