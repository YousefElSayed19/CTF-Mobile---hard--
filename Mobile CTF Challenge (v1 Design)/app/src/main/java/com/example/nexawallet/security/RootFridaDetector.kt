package com.example.nexawallet.security

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * نقطة الفحص الأولى (مستقلة) لاكتشاف Root / Frida.
 * مبنية على فحص خصائص النظام وملفات شائعة لأجهزة الـ root.
 *
 * ملاحظة تصميمية: دي مقصودة تكون "نقطة الفحص الأولى" المنفصلة عن
 * النقطة التانية الموجودة في TransferActivity، عشان اللاعب لو
 * عطّل واحدة بس هتفضل التانية شغالة وتمسكه.
 */
object RootFridaDetector {

    private val SUSPICIOUS_PATHS = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    fun isDeviceCompromised(): Boolean {
        return checkRootFiles() || checkFridaPort()
    }

    private fun checkRootFiles(): Boolean {
        for (path in SUSPICIOUS_PATHS) {
            if (File(path).exists()) return true
        }
        return false
    }

    /**
     * Frida server بيفتح افتراضيًا على بورت 27042.
     * ده فحص بسيط وساذج عمدًا (سهل الالتفاف عليه لو عدّلت بورت frida-server)،
     * عشان يبقى تحدي حقيقي مش مستحيل.
     */
    private fun checkFridaPort(): Boolean {
        return try {
            val socket = java.net.Socket()
            socket.connect(java.net.InetSocketAddress("127.0.0.1", 27042), 200)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
