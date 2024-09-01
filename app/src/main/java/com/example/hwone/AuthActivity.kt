package com.example.hwone

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
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
            if (userPreferences.isRememberMeChecked()) {
                navigateToMainActivity(userPreferences.getEmail())
            }
        }
    }

    // Customizing the user interface
    private fun setupUI() {
        enableEdgeToEdge()
        setContentView(binding.auth)
        binding.buttonRegister.isEnabled = false

        lifecycleScope.launch {
            if (userPreferences.isRememberMeChecked()) {
                binding.emailInput.setText(userPreferences.getEmail())
                binding.passInput.setText(userPreferences.getPassword())
            }
        }
    }

    // Configuring event listeners
    private fun setupListeners() {
        setupEmailInputListener()
        setupPasswordInputListener()
        setupRegisterButtonListener()
    }

    // Event listener for the Email input field
    private fun setupEmailInputListener() {
        binding.emailInput.doOnTextChanged { text, _, _, _ ->
            val email = text.toString()
            binding.emailInputLayout.error =
                if (!isValidEmail(email)) getString(R.string.errorIncorrectEmail) else null
            checkFieldsForValidation()
        }
    }

    // Event listener for the password input field
    private fun setupPasswordInputListener() {
        binding.passInput.doOnTextChanged { text, _, _, _ ->
            val password = text.toString()
            binding.passInputLayout.error =
                if (!isValidPassword(password)) getString(R.string.errorIncorrectPassword) else null
            checkFieldsForValidation()
        }
    }

    // Event listener for the registration button
    private fun setupRegisterButtonListener() {
        binding.buttonRegister.setOnClickListener {
            lifecycleScope.launch {
                val email = binding.emailInput.text.toString()
                val password = binding.passInput.text.toString()
                val rememberMe = binding.checkBox.isChecked
                userPreferences.saveLoginData(email,password,rememberMe)
                navigateToMainActivity(email)
            }
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

    // Checking the fields to activate the registration button
    private fun checkFieldsForValidation() {
        val email = binding.emailInput.text.toString()
        val password = binding.passInput.text.toString()
        binding.buttonRegister.isEnabled = isValidEmail(email) && isValidPassword(password)
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
