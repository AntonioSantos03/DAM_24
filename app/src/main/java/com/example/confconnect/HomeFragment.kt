package com.example.confconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapters.RvArticlesAdapter
import com.example.confconnect.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.type.LatLng

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articlesList: ArrayList<Articles>
    private lateinit var rvArticlesAdapter: RvArticlesAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var locationsRef: DatabaseReference

    private var savedLocationId: String? = null
    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articlesList = ArrayList()
        rvArticlesAdapter = RvArticlesAdapter(articlesList)

        database = FirebaseDatabase.getInstance()
        locationsRef = database.reference.child("locations")

        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.adapter = rvArticlesAdapter

        rvArticlesAdapter.setOnItemClickListener { article ->
            val intent = Intent(activity, ArticleDetails::class.java).apply {
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

        binding.btnAdminChat.setOnClickListener {
            val intent = Intent(activity, CommunityChat::class.java)
            startActivity(intent)
        }

        binding.btnOpenMapActivity.setOnClickListener {
            if (savedLocationId != null && selectedLocation != null) {
                val intent = Intent(activity, ShowMap::class.java).apply {
                    putExtra("locationId", savedLocationId)
                    putExtra("coordinates", "${selectedLocation!!.latitude},${selectedLocation!!.longitude}")
                }
                startActivity(intent)
            } else {
                Toast.makeText(context, "No location data available", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "No location data available: savedLocationId=$savedLocationId, selectedLocation=$selectedLocation")
            }
        }

        // Set up search view
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchArticles(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchArticles(it) }
                return true
            }
        })

        fetchArticlesData()
        fetchLocationData()
    }

    private fun fetchArticlesData() {
        val database = FirebaseDatabase.getInstance().getReference("Articles")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                articlesList.clear()
                for (dataSnapshot in snapshot.children) {
                    val article = dataSnapshot.getValue(Articles::class.java)
                    article?.let {
                        articlesList.add(it)
                    }
                }
                rvArticlesAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Articles fetched successfully.")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load articles", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Failed to load articles: ${error.message}")
            }
        })
    }

    private fun fetchLocationData() {
        locationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        savedLocationId = dataSnapshot.key
                        val coordinates = dataSnapshot.child("coordinates").getValue(String::class.java)
                        if (coordinates != null) {
                            val parts = coordinates.split(",")
                            if (parts.size == 2) {
                                try {
                                    val lat = parts[0].toDouble()
                                    val lng = parts[1].toDouble()
                                    selectedLocation = LatLng.newBuilder().setLatitude(lat).setLongitude(lng).build()
                                    Log.d("HomeFragment", "Location fetched: ${selectedLocation.toString()}")
                                } catch (e: NumberFormatException) {
                                    Log.e("HomeFragment", "Error parsing coordinates", e)
                                }
                            } else {
                                Log.e("HomeFragment", "Invalid coordinates format")
                            }
                        } else {
                            Log.e("HomeFragment", "Coordinates not found")
                        }
                        break
                    }
                } else {
                    Log.d("HomeFragment", "No location data found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to load location data: ${error.message}")
            }
        })
    }

    private fun searchArticles(query: String) {
        val filteredList = articlesList.filter {
            (it.title?.contains(query, true) ?: false) ||
                    (it.author?.contains(query, true) ?: false) ||
                    (it.description?.contains(query, true) ?: false)
        }
        rvArticlesAdapter.updateList(ArrayList(filteredList))
        Log.d("HomeFragment", "Search results updated.")
    }
}
