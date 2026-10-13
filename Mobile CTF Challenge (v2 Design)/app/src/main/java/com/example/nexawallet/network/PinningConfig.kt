package com.example.nexawallet.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

/**
 * نقطة SSL Pinning الأولى: طبقة الشبكة العادية (OkHttp).
 * ده المكان اللي بيتعامل مع كل طلبات الـ API العادية (login, balance).
 *
 * ملاحظة: القيمة دي placeholder - لازم تتستبدل بالـ pin الحقيقي
 * بتاع شهادة سيرفر التحدي لما يتربط فعليًا.
 */
object PinningConfig {

    private const val API_HOSTNAME = "api.nexawallet-ctf.local"

    // placeholder pin - استبدلها بـ: openssl s_client -connect host:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | sha256sum | base64
    private const val SERVER_PIN = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="

    fun buildClient(): OkHttpClient {
        val pinner = CertificatePinner.Builder()
            .add(API_HOSTNAME, SERVER_PIN)
            .build()

        return OkHttpClient.Builder()
            .certificatePinner(pinner)
            .build()
    }
}
