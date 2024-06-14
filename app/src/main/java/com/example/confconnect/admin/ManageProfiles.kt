package com.example.confconnect.admin

import com.example.confconnect.UserDetails
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
import com.google.firebase.database.*

class ManageProfiles : AppCompatActivity() {

    private lateinit var binding: ActivityManageProfilesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var userList: ArrayList<Users>
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        userList = ArrayList()
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList) { user -> manageUser(user) }
        binding.rvUsers.adapter = userAdapter

        fetchUsers()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchUsers() {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear() // Clear the list to avoid duplicates
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    user?.let {
                        userList.add(it)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageProfiles, "Failed to fetch users: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun manageUser(user: Users) {
        val intent = Intent(this, UserDetails::class.java).apply {
            putExtra("userId", user.id)
            putExtra("userEmail", user.email)
            putExtra("userName", user.name)
        }
        startActivity(intent)
    }
}
