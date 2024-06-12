package com.example.confconnect.admin

import RvArticlesAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.AddArticle
import com.example.confconnect.Articles
import com.example.confconnect.databinding.ActivityManageArticlesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageArticles : AppCompatActivity() {

    private var _binding: ActivityManageArticlesBinding? = null
    private val binding get() = _binding!!
    private lateinit var articlesList: ArrayList<Articles>
    private lateinit var rvArticlesAdapter: RvArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityManageArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articlesList = ArrayList()
        rvArticlesAdapter = RvArticlesAdapter(articlesList)

        binding.rvArticles.layoutManager = LinearLayoutManager(this)
        binding.rvArticles.adapter = rvArticlesAdapter

        rvArticlesAdapter.setOnItemClickListener { article ->
            val intent = Intent(this, EditArticles::class.java).apply {
                putExtra("articleId", article.id)
                putExtra("title", article.title)
                putExtra("author", article.author)
                putExtra("date", article.date)
                putExtra("description", article.description)
            }
            startActivity(intent)
        }

        fetchArticlesData()

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddArticle::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchArticlesData() {
        val database = FirebaseDatabase.getInstance().getReference("Articles")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                articlesList.clear()
                for (dataSnapshot in snapshot.children) {
                    val article = dataSnapshot.getValue(Articles::class.java)
                    if (article != null) {
                        articlesList.add(article)
                    }
                }
                rvArticlesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageArticles, "Failed to load articles", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
