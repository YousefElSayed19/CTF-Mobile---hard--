package com.example.nexawallet.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.databinding.ActivityWebTransferBinding
import com.example.nexawallet.network.PinnedWebViewClient

/**
 * شاشة اختيارية تستخدم WebView لعرض جزء من عملية التحويل
 * (مثلاً: صفحة تأكيد OTP). دي موجودة أساسًا عشان تستضيف
 * نقطة الـ SSL Pinning الثانية (PinnedWebViewClient) المنفصلة
 * عن طبقة OkHttp العادية.
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
