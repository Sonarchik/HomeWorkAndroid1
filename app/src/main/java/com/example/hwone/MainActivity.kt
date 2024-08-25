package com.example.hwone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferences
    private lateinit var nameTextView: TextView
    private lateinit var avatarIcon: CircleImageView
    private lateinit var settingsTextView: AppCompatTextView
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        userPreferences = UserPreferences(this)
        initializeViews()
        setupInsets()
        loadUserData()

        settingsTextView.setOnClickListener {
            openSettings()
        }

        logOutButton.setOnClickListener {
            logoutUser()
        }

        onBackPressedDispatcher.addCallback(this) {
            handleOnBackPressed()
        }
    }

    private fun initializeViews() {
        nameTextView = findViewById(R.id.textFullName)
        avatarIcon = findViewById(R.id.iconProfile)
        settingsTextView = findViewById(R.id.textSettings)
        logOutButton = findViewById(R.id.buttonLogOut)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUserData() {
        val email = userPreferences.getEmail() ?: getString(R.string.userFullName)
        val name = parseEmailToName(email)
        updateUI(name)
    }

    private fun parseEmailToName(email: String): String {
        return email.substringBefore("@")
            .split(".")
            .joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
    }

    private fun updateUI(name: String) {
        avatarIcon.setImageResource(R.drawable.icon1)
        nameTextView.text = name
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    private fun logoutUser() {
        userPreferences.logout()
        navigateToAuthActivity()
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this@MainActivity, AuthActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun handleOnBackPressed() {
        finish()
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
    }
}