package com.example.nexawallet.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityTransferBinding
import com.example.nexawallet.security.MapsIntegrityCheck
import com.example.nexawallet.security.RootFridaDetector
import com.example.nexawallet.util.MockBackend
import com.example.nexawallet.util.SessionManager
import java.security.MessageDigest

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private var limitPiastres: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rootOrFridaDetected = RootFridaDetector.isDeviceCompromised()
        val memoryTampered = MapsIntegrityCheck.isMemoryTampered()

        if (rootOrFridaDetected || memoryTampered) {
            startActivity(Intent(this, BlockedActivity::class.java))
            finish()
            return
        }

        val username = SessionManager.currentUsername ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        limitPiastres = MockBackend.getMaxTransferLimit(this, username)
        binding.tvMaxLimitInfo.text = "Max transfer limit: EGP %.2f".format(limitPiastres / 100.0)

        binding.btnConfirmTransfer.setOnClickListener {
            handleTransferClick(username)
        }
    }

    private fun handleTransferClick(username: String) {
        val amountText = binding.etAmount.text.toString()
        val amountEgp = amountText.toDoubleOrNull()

        if (amountEgp == null || amountEgp <= 0) {
            binding.tvTransferResult.text = "Invalid amount"
            return
        }

        val amountPiastres = Math.round(amountEgp * 100)

        val approved = validateOp(amountPiastres)

        if (!approved) {
            binding.tvTransferResult.text = "Transaction declined: amount exceeds allowed limit"
            return
        }

        MockBackend.deductBalance(this, username, amountPiastres)

        if (amountPiastres > limitPiastres) {
            val token = generateBypassToken(username, amountPiastres)
            binding.tvTransferResult.text =
                "✅ Transfer approved beyond limit!\n\nToken:\n$token"
        } else {
            binding.tvTransferResult.text = "Transfer completed successfully within limit"
        }
    }


    private fun validateOp(requestedAmount: Long): Boolean {
        val sessionOk = SessionManager.sessionApproved
        return sessionOk && requestedAmount <= limitPiastres
    }

    private fun generateBypassToken(username: String, amount: Long): String {
        val raw = "$username:$amount:${System.currentTimeMillis() / 100_000}:nexawallet-local-secret"
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        val hex = digest.joinToString("") { "%02x".format(it) }.take(32)
        return "duck{$hex}"
    }
}
