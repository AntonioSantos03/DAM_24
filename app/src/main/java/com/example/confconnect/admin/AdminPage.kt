package com.example.confconnect.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Login
import com.example.confconnect.R
import com.google.firebase.auth.FirebaseAuth

class AdminPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        val manageArticlesBtn = findViewById<Button>(R.id.btnManageArticles)
        val manageScheduleBtn = findViewById<Button>(R.id.btnManageSchedule)
        val manageProfilesBtn = findViewById<Button>(R.id.btnManageProfiles)
        val manageCommentsBtn = findViewById<Button>(R.id.btnManageComments)
        val manageLocationBtn = findViewById<Button>(R.id.btnManageLocation)
        val logoutBtn = findViewById<ImageButton>(R.id.logoutBtn)

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        }

        manageArticlesBtn.setOnClickListener {
            startActivity(Intent(this, ManageArticles::class.java))
        }

        manageScheduleBtn.setOnClickListener {
            startActivity(Intent(this, ManageSchedule::class.java))
        }

        manageProfilesBtn.setOnClickListener {
            startActivity(Intent(this, ManageProfiles::class.java))
        }

        manageCommentsBtn.setOnClickListener {
            startActivity(Intent(this, ManageComments::class.java))
        }

        manageLocationBtn.setOnClickListener {
            startActivity(Intent(this, ManageLocation::class.java))
        }
    }
}
