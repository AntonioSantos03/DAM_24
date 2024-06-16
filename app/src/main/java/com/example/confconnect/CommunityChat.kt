package com.example.confconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.adapter.ChatAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommunityChat : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonBack: FloatingActionButton
    private lateinit var recyclerViewChat: RecyclerView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: ChatAdapter
    private val chatList: MutableList<ChatMessage> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_chat)

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Initialize views
        buttonBack = findViewById(R.id.btnBack)
        editTextMessage = findViewById(R.id.edit_text_message)
        buttonSend = findViewById(R.id.button_send)
        recyclerViewChat = findViewById(R.id.recycler_view_chat)

        // Setup RecyclerView
        recyclerViewChat.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(chatList)
        recyclerViewChat.adapter = adapter

        buttonBack.setOnClickListener {
            finish()
        }

        // Setup button click listener
        buttonSend.setOnClickListener {
            sendMessage()
        }

        // Listen for incoming messages
        database.child("messages").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    chatList.add(it)
                    adapter.notifyItemInserted(chatList.size - 1)
                    recyclerViewChat.scrollToPosition(chatList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val messageText = editTextMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            // Ensure user is authenticated
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                // Retrieve user's name from Firebase Realtime Database
                database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val userName = snapshot.child("name").getValue(String::class.java) ?: "Anonymous"

                            // Create ChatMessage object
                            val message = ChatMessage(userId, userName, messageText)

                            // Save message to Firebase Realtime Database
                            database.child("messages").push().setValue(message)
                                .addOnSuccessListener {
                                    editTextMessage.text.clear()
                                }
                                .addOnFailureListener {
                                    // Handle any errors
                                }
                        } else {
                            // Handle case where user data doesn't exist
                            // Maybe fallback to a default name or handle it gracefully
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database error
                    }
                })
            } else {
                // Handle case where user is not authenticated
            }
        }
    }
}
