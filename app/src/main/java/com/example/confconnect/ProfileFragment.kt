package com.example.confconnect

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapter.CommentAdapter
import com.example.confconnect.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var commentsAdapter: CommentAdapter
    private lateinit var tvNoCommentsMessage: TextView

    private var sidebarVisible = false // Flag to track sidebar visibility

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize RecyclerView and its adapter
        binding.rvUserComments.layoutManager = LinearLayoutManager(requireContext())
        commentsAdapter = CommentAdapter(emptyList())
        binding.rvUserComments.adapter = commentsAdapter

        // TextView for no comments message
        tvNoCommentsMessage = binding.tvNoCommentsMessage

        // Fetch and display current user's email and name
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val email = user.email ?: "Unknown"
            binding.emailTextView.text = "Email: $email"

            // Fetch and display user's name from Firebase Realtime Database
            val usersRef = database.getReference("users").child(userId)
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    val displayName = user?.name ?: "Unknown"
                    binding.nameTextView.text = "Name: $displayName"
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error fetching user data
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch user data: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            // Fetch and display user's comments from Firebase Realtime Database
            val commentsRef = database.getReference("comments").orderByChild("userId").equalTo(userId)
            commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val commentsList = mutableListOf<Comments>()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comments::class.java)
                        comment?.let {
                            // Check if the comment is approved before adding to the list
                            if (it.approved == true) {
                                commentsList.add(it)
                            }
                        }
                    }
                    commentsAdapter.setComments(commentsList)

                    // Show/hide no comments message based on commentsList size
                    if (commentsList.isEmpty()) {
                        tvNoCommentsMessage.visibility = View.VISIBLE
                    } else {
                        tvNoCommentsMessage.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error fetching comments
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch comments: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } ?: run {
            Toast.makeText(
                requireContext(),
                "User not authenticated",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Set up click listener for Settings button
        binding.settingsBtn.setOnClickListener {
            toggleSidebar()
        }

        // Set up click listeners for sidebar options
        binding.logoutOption.setOnClickListener {
            toggleSidebar()
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
        }

        binding.deleteAccountOption.setOnClickListener {
            toggleSidebar()
            showReauthenticationDialog()
        }

        // Set up click listener for overlay to hide sidebar
        binding.overlay.setOnClickListener {
            if (sidebarVisible) {
                toggleSidebar()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleSidebar() {
        if (sidebarVisible) {
            // Hide sidebar and overlay
            binding.sidebar.visibility = View.GONE
            binding.overlay.visibility = View.GONE
            sidebarVisible = false
        } else {
            // Show sidebar and overlay
            binding.sidebar.visibility = View.VISIBLE
            binding.overlay.visibility = View.VISIBLE
            sidebarVisible = true
        }
    }

    private fun showReauthenticationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reauthenticate, null)
        val editPassword = dialogView.findViewById<EditText>(R.id.editPassword)
        val btnAuthenticate = dialogView.findViewById<Button>(R.id.btnAuthenticate)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnAuthenticate.setOnClickListener {
            val password = editPassword.text.toString().trim()

            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = firebaseAuth.currentUser
            val credential = EmailAuthProvider.getCredential(currentUser?.email ?: "", password)
            currentUser?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    currentUser.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), Login::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete account: ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Authentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}
