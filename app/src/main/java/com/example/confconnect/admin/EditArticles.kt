package com.example.confconnect.admin

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.ArticleDetails
import com.example.confconnect.Articles
import com.example.confconnect.databinding.ActivityEditArticlesBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class EditArticles : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

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
            time = intent.getStringExtra("time") ?: "",
            description = intent.getStringExtra("description") ?: "",
            room = intent.getStringExtra("room") ?: "",
        )

        // Bind data to views
        binding.etTitle.setText(article.title)
        binding.etAuthor.setText(article.author)
        binding.etDate.setText(article.date)
        binding.etTime.setText(article.time) // Set the time EditText with the retrieved time
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

        // Set click listener for time EditText to show TimePicker dialog
        binding.etTime.setOnClickListener {
            showTimePicker()
        }
    }

    // Method to show TimePicker dialog
    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        TimePickerDialog(this, this, hour, minute, true).show()
    }

    // Callback method when a time is set in TimePicker
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Format the selected time as needed (e.g., convert to AM/PM format)
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)

        // Set the selected time to the EditText
        binding.etTime.setText(formattedTime)
    }

    private fun previewArticle() {
        val intent = Intent(this, ArticleDetails::class.java).apply {
            putExtra("articleId", article.articleId)
            putExtra("title", article.title)
            putExtra("author", article.author)
            putExtra("date", article.date)
            putExtra("description", article.description)
            putExtra("room", article.room)
            putExtra("time", article.time)
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
            room = binding.etRoom.text.toString(),
            time = binding.etTime.text.toString()
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
