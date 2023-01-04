package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.EditPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var _binding:FragmentFeedBinding? = null
    private val binding:FragmentFeedBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                    textArg = post.content
                })
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                post.id?.let { viewModel.likeById(it) }
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
                viewModel.removeById(post.id)
            }
            override fun onVideoClick(post: Post) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    startActivity(intent)
                }
            override fun onPost(post: Post){
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
                viewModel.getPost(post.id)
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner)
        { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(viewLifecycleOwner)
        { post ->
            if (post.id == 0L) {
                return@observe
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }
}

