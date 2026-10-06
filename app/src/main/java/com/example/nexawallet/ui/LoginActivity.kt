package com.example.nexawallet.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityLoginBinding
import com.example.nexawallet.util.MockBackend
import com.example.nexawallet.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                binding.tvLoginError.text = "Please enter username and password"
                return@setOnClickListener
            }

            if (MockBackend.login(this, username, password)) {
                SessionManager.onLoginSuccess(username)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                binding.tvLoginError.text = "Invalid credentials"
            }
        }

        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
