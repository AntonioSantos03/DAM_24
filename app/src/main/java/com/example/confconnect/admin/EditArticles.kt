package com.example.confconnect.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Articles
import com.example.confconnect.ArticleDetails
import com.example.confconnect.databinding.ActivityEditArticlesBinding
import com.google.firebase.database.FirebaseDatabase

class EditArticles : AppCompatActivity() {

    private lateinit var binding: ActivityEditArticlesBinding
    private lateinit var article: Articles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get article data from intent
        article = Articles(
            articleId = intent.getStringExtra("Id") ?: "",
            title = intent.getStringExtra("title") ?: "",
            author = intent.getStringExtra("author") ?: "",
            date = intent.getStringExtra("date") ?: "",
            description = intent.getStringExtra("description") ?: "",
            room = intent.getStringExtra("room") ?: ""
        )

        // Bind data to views
        binding.etTitle.setText(article.title)
        binding.etAuthor.setText(article.author)
        binding.etDate.setText(article.date)
        binding.etDescription.setText(article.description)
        binding.etRoom.setText(article.room)

        // Set up listeners for save and delete button
        binding.btnPreview.setOnClickListener {
            previewArticle()
        }

        binding.btnSave.setOnClickListener {
            saveArticle()
        }

        binding.btnDelete.setOnClickListener {
            deleteArticle()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun previewArticle() {
        val intent = Intent(this, ArticleDetails::class.java).apply {
            putExtra("articleId", article.articleId)
            putExtra("title", article.title)
            putExtra("author", article.author)
            putExtra("date", article.date)
            putExtra("description", article.description)
            putExtra("room", article.room)
        }
        startActivity(intent)
    }

    private fun saveArticle() {
        val database = FirebaseDatabase.getInstance().getReference("Articles")
        val updatedArticle = Articles(
            articleId = article.articleId,
            title = binding.etTitle.text.toString(),
            author = binding.etAuthor.text.toString(),
            date = binding.etDate.text.toString(),
            description = binding.etDescription.text.toString(),
            room = binding.etRoom.text.toString()
        )

        article.articleId?.let {
            database.child(it).setValue(updatedArticle)
                .addOnSuccessListener {
                    Toast.makeText(this, "Article updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update article", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteArticle() {
        val database = FirebaseDatabase.getInstance().getReference("Articles")
        article.articleId?.let {
            database.child(it).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Article deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete article", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
