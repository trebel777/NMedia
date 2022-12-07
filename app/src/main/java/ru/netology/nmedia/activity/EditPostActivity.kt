package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityEditPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()
        val intent = Intent()
        val input = Post(
            getIntent().getLongExtra("id", 0),
            getIntent().getStringExtra("author").toString(),
            getIntent().getStringExtra("content").toString(),
            getIntent().getStringExtra("published").toString(),
            getIntent().getStringExtra("video").toString(),
            getIntent().getLongExtra("likes", 0),
            getIntent().getLongExtra("replys", 0),
            getIntent().getBooleanExtra("likedByMe", false),
            getIntent().getBooleanExtra("replyByMe", false)
        )
        binding.editTextForEdit.setText(input.content)
        binding.okEdit.setOnClickListener {
            if (binding.editTextForEdit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.editTextForEdit.text.toString()
                intent.putExtra("id", input.id)
                intent.putExtra("author", input.author)
                intent.putExtra("content", content)
                intent.putExtra("published", input.published)
                intent.putExtra("likedByMe", input.likedByMe)
                intent.putExtra("likes", input.likes)
                setResult(Activity.RESULT_OK, intent)
            }

            finish()
        }
        binding.cancelEdit.setOnClickListener {
            with(binding.editTextForEdit) {
                viewModel.cancelEdit()
                setText("")
                clearFocus()
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}





