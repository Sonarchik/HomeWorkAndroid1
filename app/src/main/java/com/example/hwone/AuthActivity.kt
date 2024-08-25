package com.example.hwone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferencesInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Виберіть, яку реалізацію використовувати: SharedPreferences або DataStore
//        userPreferences = UserPreferencesDataStore(this) // або UserPreferencesSharedPrefs(this)
        userPreferences = UserPreferencesSharedPrefs(this) // або UserPreferencesDataStore(this)

        lifecycleScope.launch {
            if (userPreferences.isLoggedIn()) {
                navigateToMainActivity(userPreferences.getEmail())
                return@launch
            }
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.EmailInputLayout)
        val emailInput = findViewById<TextInputEditText>(R.id.EmailInput)
        val passInputLayout = findViewById<TextInputLayout>(R.id.PassInputLayout)
        val passInput = findViewById<TextInputEditText>(R.id.PassInput)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.isEnabled = false

        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (!isValidEmail(email)) {
                    emailInputLayout.error = getString(R.string.errorIncorrectEmail)
                } else {
                    emailInputLayout.error = null
                }
                checkFieldsForValidation(emailInput, passInput, registerButton)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        passInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (!isValidPassword(password)) {
                    passInputLayout.error = getString(R.string.errorIncorrectPassword)
                } else {
                    passInputLayout.error = null
                }
                checkFieldsForValidation(emailInput, passInput, registerButton)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        registerButton.setOnClickListener {
            lifecycleScope.launch {
                val email = emailInput.text.toString()
                userPreferences.saveLoginData(email)
                navigateToMainActivity(email)
            }
        }
    }

    private fun checkFieldsForValidation(emailInput: TextInputEditText, passInput: TextInputEditText, registerButton: Button) {
        val email = emailInput.text.toString()
        val password = passInput.text.toString()
        registerButton.isEnabled = isValidEmail(email) && isValidPassword(password)
    }

    private fun navigateToMainActivity(email: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }
}
