package com.example.nexawallet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityRestrictedBinding

class RestrictedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestrictedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestrictedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
