package com.example.hwone

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.databinding.ActivityAuthBinding
import com.example.hwone.userdto.UserPreferencesDataStore
import com.example.hwone.userdto.UserPreferencesInterface
import com.example.hwone.userdto.UserPreferencesSharedPrefs
import com.example.hwone.utils.ValidationUtils.isValidEmail
import com.example.hwone.utils.ValidationUtils.isValidPassword
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val binding: ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }
    private val userPreferences: UserPreferencesInterface by lazy {
        // Choose the desired implementation: SharedPreferences or DataStore
        UserPreferencesDataStore(this) // or UserPreferencesSharedPrefs(this)
//        UserPreferencesSharedPrefs(this) // or UserPreferencesDataStore(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkRememberMe()
        super.onCreate(savedInstanceState)
        setupUI()
        setupListeners()
        applyWindowInsets()
    }

    //Checking whether the Remember me check box is present
    private fun checkRememberMe() {
        lifecycleScope.launch {
            userPreferences.isRememberMeChecked().collect { rememberMeChecked ->
                if (rememberMeChecked == true) {
                    userPreferences.getEmail().collect { email ->
                        navigateToMainActivity(email)
                    }
                }
            }
        }
    }

    // Customizing the user interface
    private fun setupUI() {
        enableEdgeToEdge()
        setContentView(binding.auth)

        lifecycleScope.launch {
            userPreferences.isRememberMeChecked().collect { rememberMeChecked ->
                if (rememberMeChecked == true) {
                    userPreferences.getEmail().collect { email ->
                        binding.emailInput.setText(email)
                    }
                    userPreferences.getPassword().collect { password ->
                        binding.passInput.setText(password)
                    }
                }
            }
        }
    }

    // Configuring event listeners
    private fun setupListeners() {
        setupRegisterButtonListener()
    }

    // Event handler for the registration button
    private fun setupRegisterButtonListener() {
        binding.buttonRegister.setOnClickListener {
            // We check the correctness of the data
            if (validateFields()) {
                lifecycleScope.launch {
                    val email = binding.emailInput.text.toString()
                    val password = binding.passInput.text.toString()
                    val rememberMe = binding.checkBox.isChecked
                    userPreferences.saveLoginData(email, password, rememberMe)
                    navigateToMainActivity(email)
                }
            }
        }
    }

    // Checking the fields and outputting errors, if any
    private fun validateFields(): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()

        return isEmailValid && isPasswordValid
    }

    // Email verification
    private fun validateEmail(): Boolean {
        val email = binding.emailInput.text.toString()

        return if (!isValidEmail(email)) {
            binding.emailInputLayout.helperText = getString(R.string.errorIncorrectEmail)
            binding.emailInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))
            false
        } else {
            binding.emailInputLayout.helperText = null
            true
        }
    }

    // Password verification
    private fun validatePassword(): Boolean {
        val password = binding.passInput.text.toString()

        return if (!isValidPassword(password)) {
            binding.passInputLayout.helperText = getString(R.string.errorIncorrectPassword)
            binding.passInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED))
            false
        } else {
            binding.passInputLayout.helperText = null
            true
        }
    }

    // Application of indents for window inserts
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Go to MainActivity
    private fun navigateToMainActivity(email: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EXTRA_EMAIL, email)

        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        startActivity(intent, options.toBundle())
        finish()
    }
}
