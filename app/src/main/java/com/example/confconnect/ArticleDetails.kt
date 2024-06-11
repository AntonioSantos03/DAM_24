package com.example.confconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.databinding.ActivityArticleDetailsBinding

class ArticleDetails : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")

        binding.tvTitle.text = title
        binding.tvAuthor.text = author
        binding.tvDate.text = date
        binding.tvDescription.text = description


        binding.btnBack.setOnClickListener {
            // Go back to the previous screen
            finish()
        }
    }

}
