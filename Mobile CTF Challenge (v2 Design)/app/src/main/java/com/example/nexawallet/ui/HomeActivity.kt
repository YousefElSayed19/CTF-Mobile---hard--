package com.example.nexawallet.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.data.LocalLedger
import com.example.nexawallet.databinding.ActivityHomeBinding
import com.example.nexawallet.session.UserSession

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = UserSession.currentUsername ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        refreshUi(username)

        binding.btnGoTransfer.setOnClickListener {
            startActivity(Intent(this, TransferActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        UserSession.currentUsername?.let { refreshUi(it) }
    }

    private fun refreshUi(username: String) {
        val balance = LocalLedger.getBalance(this, username) / 100.0
        val limit = LocalLedger.getMaxTransferLimit(this, username) / 100.0

        binding.tvWelcome.text = "Welcome, $username"
        binding.tvBalance.text = "EGP %.2f".format(balance)
        binding.tvLimit.text = "Max transfer limit: EGP %.2f".format(limit)
    }
}
