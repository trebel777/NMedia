package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.SignUpViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signUp.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            if (binding.pass.text.toString() != binding.passRepeat.text.toString()) {
                Snackbar.make(binding.root, "Passwords don't match ", Snackbar.LENGTH_LONG).show()
            } else {
                viewModel.name.value = binding.name.text.toString()
                viewModel.login.value = binding.login.text.toString()
                viewModel.pass.value = binding.pass.text.toString()
                viewModel.signUp()
            }

        }
        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changeAvatar(uri, uri?.toFile())
                    }
                }
            }
        binding.uploadAvatar.setOnClickListener {
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(resources.getString(R.string.choose))
                .setNegativeButton(R.string.gallery) { _, _ ->
                    ImagePicker.with(this)
                        .galleryOnly()
                        .crop()
                        .compress(192)
                        .createIntent(pickPhotoLauncher::launch)
                }
                .setPositiveButton(R.string.photo) { _, _ ->
                    ImagePicker.with(this)
                        .cameraOnly()
                        .crop()
                        .compress(192)
                        .createIntent(pickPhotoLauncher::launch)
                }
                .show()
        }

        binding.removePhoto.setOnClickListener {
            viewModel.clearAvatar(null, null)
        }
        viewModel.avatar.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            auth.setAuth(state.id, state.token!!)
            findNavController().navigateUp()
        }

        viewModel.userAuthResult.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, getString(R.string.reg_error), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        return binding.root
    }
}