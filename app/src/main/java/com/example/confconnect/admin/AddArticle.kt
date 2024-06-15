package com.example.confconnect.admin

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Articles
import com.example.confconnect.databinding.ActivityAddArticleBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddArticle : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding
    private lateinit var database: DatabaseReference

    private fun saveArticleData() {
        val title = binding.title.text.toString()
        val author = binding.author.text.toString()
        val date = binding.date.text.toString()
        val time = binding.time.text.toString()
        val description = binding.description.text.toString()
        val room = binding.room.text.toString()

        val articleId = database.push().key
        val article = Articles(articleId, title, author, date, time, room, description)

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

        binding.time.apply {
            inputType = android.text.InputType.TYPE_NULL
            setOnClickListener {
                showTimePickerDialog(this)
            }
        }
    }

    private fun showTimePickerDialog(timeEditText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeEditText.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
