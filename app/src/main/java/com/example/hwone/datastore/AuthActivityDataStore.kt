package com.example.hwone.datastore

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
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.hwone.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AuthActivityDataStore : AppCompatActivity() {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passInputLayout: TextInputLayout
    private lateinit var passInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var userPreferencesDataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferencesDataStore = UserPreferencesDataStore(this)

        checkIfUserIsLoggedIn()

        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        initializeViews()
        setupInsets()
        setupValidation()

        onBackPressedDispatcher.addCallback(this) {
            handleOnBackPressed()
        }

        registerButton.setOnClickListener {
            handleRegistration()
        }
    }

    private fun checkIfUserIsLoggedIn() {
        lifecycleScope.launch {
            if (userPreferencesDataStore.isLoggedIn.first()) {
                val email = userPreferencesDataStore.email.firstOrNull()
                navigateToMainActivity(email)
            }
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
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
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
        lifecycleScope.launch {
            userPreferencesDataStore.saveLoginData(email)
            navigateToMainActivity(email)
        }
    }

    private fun navigateToMainActivity(email: String?) {
        val intent = Intent(this, MainActivityDataStore::class.java)
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

