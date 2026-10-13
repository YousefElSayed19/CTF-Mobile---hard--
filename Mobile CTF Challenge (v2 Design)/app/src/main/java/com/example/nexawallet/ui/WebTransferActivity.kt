package com.example.nexawallet.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityWebTransferBinding
import com.example.nexawallet.network.PinnedWebViewClient

/**
 * Optional screen using a WebView for part of the transfer flow
 * (e.g. an OTP confirmation page). Exists mainly to host the second
 * SSL Pinning checkpoint (PinnedWebViewClient), kept independent of
 * the standard OkHttp layer.
 */
class WebTransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebTransferBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = PinnedWebViewClient()
        binding.webView.loadUrl("https://api.nexawallet-ctf.local/transfer-confirm")
    }
}
