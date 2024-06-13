package com.example.confconnect.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Articles
import com.example.confconnect.databinding.ActivityAddArticleBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddArticle : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding
    private lateinit var database: DatabaseReference

    private fun saveArticleData() {
        val title = binding.title.text.toString()
        val author = binding.author.text.toString()
        val date = binding.date.text.toString()
        val description = binding.description.text.toString()
        val room = binding.room.text.toString()

        val articleId = database.push().key
        val article = Articles(articleId, title, author, date, room, description)

        if (articleId != null) {
            database.child(articleId).setValue(article).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Article added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to add article", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Articles")

        binding.addArticleButton.setOnClickListener {
            saveArticleData()
        }

        binding.btnBack.setOnClickListener {
            // Go back to the previous screen
            finish()
        }
    }
}
