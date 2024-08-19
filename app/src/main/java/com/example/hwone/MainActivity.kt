package com.example.hwone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val settingsTextView = findViewById<AppCompatTextView>(R.id.textSettings)
        val logOutButton = findViewById<Button>(R.id.buttonLogOut)
// Приймаємо електронну пошту з AuthActivity
        val email = intent.getStringExtra("email")
        val nameTextView = findViewById<TextView>(R.id.textFullName)
        val avatarIcon = findViewById<CircleImageView>(R.id.iconProfile)

        // Обробляємо електронну пошту, щоб перетворити її на ім'я
        val name = parseEmailToName(email ?: getString(R.string.userFullName))

        // Відображаємо ім'я на екрані
//        avatar.setImageResource(R.drawable.icon1)
        nameTextView.text = name



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsTextView.setOnClickListener {
            // Інтент для переходу в загальні налаштування
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {

            // Переходимо на екран AuthActivity
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Анімація для повернення назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Функція для обробки електронної пошти та перетворення її на ім'я
    private fun parseEmailToName(email: String): String {
            val namePart = email.substringBefore("@") // Беремо частину до @
            val nameParts = namePart.split(".")
                .joinToString(" ") { it -> it.replaceFirstChar { it.uppercase() } }
            return nameParts


    }
}