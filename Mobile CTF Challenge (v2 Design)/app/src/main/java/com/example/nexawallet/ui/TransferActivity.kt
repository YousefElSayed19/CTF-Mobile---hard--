package com.example.nexawallet.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.authorization.TransferAuthorizer
import com.example.nexawallet.data.LocalLedger
import com.example.nexawallet.databinding.ActivityTransferBinding
import com.example.nexawallet.integrity.IntegrityGate
import com.example.nexawallet.session.UserSession

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private var limitPiastres: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tampered = IntegrityGate.isTampered()
        Log.d("NexaWalletDebug", "IntegrityGate.isTampered() returned: $tampered")
        if (tampered) {
            startActivity(Intent(this, RestrictedActivity::class.java))
            finish()
            return
        }

        val username = UserSession.currentUsername ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        limitPiastres = LocalLedger.getMaxTransferLimit(this, username)
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

        val allowed = TransferAuthorizer.isTransferAllowed(amountPiastres, limitPiastres)

        if (!allowed) {
            binding.tvTransferResult.text = "Transaction declined: amount exceeds allowed limit"
            return
        }

        LocalLedger.deductBalance(this, username, amountPiastres)

        if (amountPiastres > limitPiastres) {
            val token = TransferAuthorizer.mintClearanceToken(username, amountPiastres)
            binding.tvTransferResult.text =
                "✅ Transfer approved beyond limit!\n\nToken:\n$token"
        } else {
            binding.tvTransferResult.text = "Transfer completed successfully within limit"
        }
    }
}
