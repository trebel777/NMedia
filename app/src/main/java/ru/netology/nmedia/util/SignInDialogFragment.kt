package ru.netology.nmedia.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R


class SignInDialogFragment : DialogFragment() {
    @Override
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.sign_in_dialog))
                .setPositiveButton(R.string.sign_in)
                    { _, _ ->
                        findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                    }
                .setNegativeButton(getString(R.string.dialog_cancel))
                    { _, _ ->
                        findNavController().navigateUp()
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}