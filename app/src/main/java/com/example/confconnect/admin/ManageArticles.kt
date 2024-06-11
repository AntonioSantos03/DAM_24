package com.example.confconnect.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.AddArticle
import com.example.confconnect.R
import com.example.confconnect.databinding.ActivityManageArticlesBinding

class ManageArticles : AppCompatActivity() {

    private var _binding: ActivityManageArticlesBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityManageArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddArticle::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            // Go back to the previous screen
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
