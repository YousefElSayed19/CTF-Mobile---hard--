package com.example.nexawallet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityRegisterBinding
import com.example.nexawallet.util.MockBackend

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val username = binding.etRegUsername.text.toString().trim()
            val password = binding.etRegPassword.text.toString()

            if (username.length < 3 || password.length < 4) {
                binding.tvRegError.text = "Username must be 3+ characters, password 4+ characters"
                return@setOnClickListener
            }

            val success = MockBackend.register(this, username, password)
            if (success) {
                binding.tvRegError.setTextColor(0xFF2E7D32.toInt())
                binding.tvRegError.text = "Account created, go back and login"
            } else {
                binding.tvRegError.text = "This username is already taken"
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}