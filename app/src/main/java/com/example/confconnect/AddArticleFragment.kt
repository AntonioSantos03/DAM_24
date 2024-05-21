package com.example.confconnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.confconnect.databinding.FragmentAddArticleBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddArticleFragment : Fragment() {
    private var _binding: FragmentAddArticleBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddArticleBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().reference.child("Articles")

        binding.addButton.setOnClickListener {
            saveData()
        }
        binding.btnBack.setOnClickListener(){
            val fragment = HomeFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id. home_main, fragment) //
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    private fun saveData() {
        val title = binding.title.text.toString()
        val author = binding.author.text.toString()
        val date = binding.date.text.toString()
        val description = binding.description.text.toString()

        if (title.isEmpty()) binding.title.error = "This field is required"
        if (author.isEmpty()) binding.author.error = "This field is required"
        if (date.isEmpty()) binding.date.error = "This field is required"
        if (description.isEmpty()) binding.description.error = "This field is required"

        val articleID = firebaseRef.push().key!!
        val article = Articles(articleID, title, author, date, description)

        firebaseRef.child(articleID).setValue(article).addOnCompleteListener {
            Toast.makeText(context, "Article added successfully", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener() {
                Toast.makeText(context, "Failed to add article", Toast.LENGTH_SHORT).show()
            }
    }
}
