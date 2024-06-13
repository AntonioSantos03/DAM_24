package com.example.confconnect.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.Login
import com.example.confconnect.Users
import com.example.confconnect.adapter.UserAdapter
import com.example.confconnect.databinding.ActivityManageProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageProfiles : AppCompatActivity() {

    private lateinit var binding: ActivityManageProfilesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userList: ArrayList<Users>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        userList = ArrayList()
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        val userAdapter = UserAdapter(userList) { user -> manageUser(user) }
        binding.rvUsers.adapter = userAdapter

        fetchUsers(userAdapter)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchUsers(userAdapter: UserAdapter) {
        firestore.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val user = document.toObject(Users::class.java)
                        userList.add(user)
                    }
                    userAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun manageUser(user: Users) {
        val intent = Intent(this, EditUser::class.java).apply {
            putExtra("userId", user.id)
            putExtra("userEmail", user.email)
        }
        startActivity(intent)
    }
}
