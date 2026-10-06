package com.example.nexawallet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityBlockedBinding

class BlockedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
