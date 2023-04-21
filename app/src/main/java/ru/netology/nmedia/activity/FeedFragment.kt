package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.SignInDialogFragment
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels()

    @Inject
    lateinit var auth: AppAuth

    val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (!authViewModel.authorized) {
                    showSignInDialog()
                } else {
                    viewModel.likeById(post)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post)
            }

            override fun onReply(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onPhotoClick(post: Post) {
                val bundle = Bundle()
                bundle.putLong("postId", post.id)
                bundle.putString("uri", post.attachment?.url)
                val navController = findNavController()
                navController.navigate(R.id.action_feedFragment_to_showPhotoFragment, bundle)
            }

        })
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefreshLayout.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }


//        @Suppress("DEPRECATION")
//        lifecycleScope.launchWhenCreated {
//            viewModel.data.collectLatest(adapter::submitData)
//        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                authViewModel.data.observe(viewLifecycleOwner){
                    adapter.refresh()
                }
            }
        }


        binding.newPosts.setOnClickListener {
                viewModel.readNewPosts()
                binding.newPosts.isVisible = false
                binding.list.smoothScrollToPosition(0)
            }

            binding.fab.setOnClickListener {
                if (!authViewModel.authorized) {
                    showSignInDialog()
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                }
            }

            binding.swipeRefreshLayout.setOnRefreshListener {
//                viewModel.refreshPosts()
//                binding.swipeRefreshLayout.isRefreshing = false
                adapter.refresh()
            }

        return binding.root
    }

    fun showSignInDialog() {
        val dialog = SignInDialogFragment()
        dialog.show(getParentFragmentManager(), getString(R.string.authentication))
    }
}

