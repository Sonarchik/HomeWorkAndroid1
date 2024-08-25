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
import androidx.lifecycle.lifecycleScope
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferencesInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Виберіть, яку реалізацію використовувати: SharedPreferences або DataStore
//        userPreferences = UserPreferencesDataStore(this) // або UserPreferencesSharedPrefs(this)
        userPreferences = UserPreferencesSharedPrefs(this) // або UserPreferencesDataStore(this)

        val settingsTextView = findViewById<AppCompatTextView>(R.id.textSettings)
        val logOutButton = findViewById<Button>(R.id.buttonLogOut)

        lifecycleScope.launch {
            val email = userPreferences.getEmail() ?: getString(R.string.userFullName)
            val nameTextView = findViewById<TextView>(R.id.textFullName)
            val avatarIcon = findViewById<CircleImageView>(R.id.iconProfile)

            val name = parseEmailToName(email)

            avatarIcon.setImageResource(R.drawable.icon1)
            nameTextView.text = name
        }

        settingsTextView.setOnClickListener {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.logout()
                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            handleOnBackPressed()
        }
    }

    private fun parseEmailToName(email: String): String {
        val namePart = email.substringBefore("@")
        return namePart.split(".")
            .joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
    }

    private fun handleOnBackPressed() {
        finish()
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
    }
}
