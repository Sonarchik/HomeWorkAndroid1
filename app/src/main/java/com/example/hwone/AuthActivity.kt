package com.example.hwone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthActivity : AppCompatActivity() {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passInputLayout: TextInputLayout
    private lateinit var passInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ініціалізація UserPreferences
        userPreferences = UserPreferences(this)

        // Перевірка, чи користувач вже залогінений
        if (userPreferences.isLoggedIn()) {
            navigateToMainActivity(userPreferences.getEmail())
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        initializeViews()
        setupInsets()
        setupValidation()

        registerButton.setOnClickListener {
            handleRegistration()
        }

        onBackPressedDispatcher.addCallback(this) {
            handleOnBackPressed()
        }
    }

    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.EmailInputLayout)
        emailInput = findViewById(R.id.EmailInput)
        passInputLayout = findViewById(R.id.PassInputLayout)
        passInput = findViewById(R.id.PassInput)
        registerButton = findViewById(R.id.buttonRegister)
        registerButton.isEnabled = false
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth)) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupValidation() {
        emailInput.addTextChangedListener(createTextWatcher(emailInputLayout) { email ->
            isValidEmail(email)
        })

        passInput.addTextChangedListener(createTextWatcher(passInputLayout) { password ->
            isValidPassword(password)
        })
    }

    private fun createTextWatcher(inputLayout: TextInputLayout, validator: (String) -> Boolean): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (!validator(text)) {
                    inputLayout.error = getString(if (inputLayout == emailInputLayout) R.string.errorIncorrectEmail else R.string.errorIncorrectPassword)
                } else {
                    inputLayout.error = null
                }
                checkFieldsForValidation()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun checkFieldsForValidation() {
        val email = emailInput.text.toString()
        val password = passInput.text.toString()
        registerButton.isEnabled = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun handleRegistration() {
        val email = emailInput.text.toString()
        userPreferences.saveLoginData(email)
        navigateToMainActivity(email)
    }

    private fun navigateToMainActivity(email: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun handleOnBackPressed() {
        finish()
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
    }
}