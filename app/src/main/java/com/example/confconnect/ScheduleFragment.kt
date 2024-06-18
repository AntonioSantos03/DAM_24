package com.example.confconnect

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapters.ScheduleArticlesAdapter
import com.example.confconnect.databinding.FragmentScheduleBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var articlesList: ArrayList<Articles>
    private lateinit var articlesAdapter: ScheduleArticlesAdapter

    private var selectedRoom: String = "All Rooms"
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articlesList = ArrayList()
        articlesAdapter = ScheduleArticlesAdapter(requireContext(), articlesList)

        binding.rvSchedule.layoutManager = LinearLayoutManager(context)
        binding.rvSchedule.adapter = articlesAdapter

        val roomOptions = arrayOf("All Rooms", "Room 1", "Room 2", "Room 3", "Room 4", "Room 5", "Room 6")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roomOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roomFilterSpinner.adapter = spinnerAdapter

        binding.roomFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRoom = roomOptions[position]
                filterArticles()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        articlesAdapter.setOnItemClickListener { article ->
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

        binding.datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        fetchArticlesData()
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
                articlesList.sortWith(compareBy({ parseDateTime(it.date, it.time) }, { parseDateTime(it.date, it.time) }))
                filterArticles()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error (e.g., log it or show a message to the user)
            }
        })
    }

    private fun parseDateTime(dateString: String?, timeString: String?): Date? {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return try {
            format.parse("$dateString $timeString")
        } catch (e: Exception) {
            null
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "${String.format("%02d", selectedDay)}-${String.format("%02d", selectedMonth + 1)}-$selectedYear"
            filterArticles()
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun filterArticles() {
        val filteredList = articlesList.filter { article ->
            (selectedRoom == "All Rooms" || article.room == selectedRoom) &&
                    (selectedDate == null || article.date == selectedDate)
        }
        articlesAdapter.updateList(ArrayList(filteredList))
    }
}
