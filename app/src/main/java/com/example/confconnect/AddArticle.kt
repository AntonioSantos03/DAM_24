package com.example.confconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
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

        if (title.isEmpty() || author.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val articleId = database.push().key
        val article = Articles(articleId, title, author, date, description)

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
