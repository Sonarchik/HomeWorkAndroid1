package com.example.hwone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val userPreferences: UserPreferencesInterface by lazy {
        // Choose the desired implementation: SharedPreferences or DataStore
        UserPreferencesDataStore(this) // or UserPreferencesSharedPrefs(this)
//        UserPreferencesSharedPrefs(this) // or UserPreferencesDataStore(this)
    }
    private var backPressedOnce = false
    private var rememberMeChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupListeners()
        applyWindowInsets()
        handleBackPress()
    }

    // Customizing the user interface
    private fun setupUI() {
        enableEdgeToEdge()
        setContentView(binding.root)
        lifecycleScope.launch {
            val email = intent.getStringExtra(EXTRA_EMAIL) ?: getString(R.string.userFullName)
            val name = parseEmailToName(email)

            binding.iconProfile.setImageResource(R.drawable.icon1)
            binding.textFullName.text = name
        }
    }

    //Event listeners are configured here
    private fun setupListeners() {
        binding.textSettings.setOnClickListener {
            openSettings()
        }

        binding.buttonLogOut.setOnClickListener {
            logoutUser()
        }
    }

    // Application of indents for window inserts
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //The logic for processing the "Back" button is implemented here.
    // Includes checking if a button has been pressed twice to exit the program or exit the activity.
    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this) {
            if (backPressedOnce) {

                finish()
            } else {
                this@MainActivity.backPressedOnce = true
                Toast.makeText(
                    this@MainActivity, getString(R.string.notificationBackButton),
                    Toast.LENGTH_SHORT
                ).show()

                lifecycleScope.launch {
                    kotlinx.coroutines.delay(2000)
                    backPressedOnce = false
                }
            }
        }
    }

    //Method to open device settings.
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    //Responsible for user exit from the system, clears data and redirects to the authorization screen.
    private fun logoutUser() {
        lifecycleScope.launch {
            userPreferences.logout()
            navigateToAuthActivity()
        }
    }

    //A method that is called after the user logs out to go to the authorization screen.
    private fun navigateToAuthActivity() {
        val intent = Intent(this@MainActivity, AuthActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    //parse the email in Full name
    private fun parseEmailToName(email: String): String {
        val namePart = email.substringBefore("@")
        return namePart.split(".")
            .joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
    }
}
