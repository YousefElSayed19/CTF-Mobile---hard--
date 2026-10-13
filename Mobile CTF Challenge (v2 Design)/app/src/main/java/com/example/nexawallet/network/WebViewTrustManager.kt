package com.example.nexawallet.network

import android.net.http.SslCertificate
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * نقطة SSL Pinning الثانية: جوه WebView شاشة التحويل (WebTransferActivity).
 *
 * دي طبقة تحقق مستقلة تمامًا عن PinningConfig (OkHttp) - بتعتمد على
 * مقارنة يدوية لبصمة الشهادة بدل ما تستخدم آلية OkHttp، وده يحاكي
 * نمط حقيقي شائع في التطبيقات اللي بتستخدم WebView لشاشات حساسة
 * (دفع، تحويلات) منفصلة عن باقي الـ networking stack.
 */
class PinnedWebViewClient : WebViewClient() {

    // بصمة SHA-256 المتوقعة لشهادة السيرفر (placeholder لحد ما يتربط سيرفر حقيقي)
    private val expectedFingerprint = "AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99"

    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: android.net.http.SslError?
    ) {
        val cert: SslCertificate? = error?.certificate
        val actualFingerprint = extractFingerprint(cert)

        if (actualFingerprint != null && actualFingerprint.equals(expectedFingerprint, ignoreCase = true)) {
            handler?.proceed()
        } else {
            handler?.cancel()
        }
    }

    private fun extractFingerprint(cert: SslCertificate?): String? {
        // في تطبيق حقيقي: استخراج الـ DER bytes وعمل SHA-256 عليها يدويًا.
        // مبسطة هنا لغرض التوضيح داخل المشروع.
        return cert?.issuedTo?.cName
    }
}
