package com.example.nexawallet.security

import java.io.BufferedReader
import java.io.FileReader

/**
 * نقطة الفحص الثانية (مستقلة تمامًا عن RootFridaDetector).
 * بتفحص /proc/self/maps بحثًا عن مكتبات محقونة معروفة (frida-agent، إلخ).
 *
 * الفكرة التصميمية: دي آلية كشف مختلفة تمامًا في المصدر عن النقطة الأولى
 * (فحص ملفات vs فحص الذاكرة)، فمحاولة تعطيل RootFridaDetector لوحدها
 * مش كافية - لازم الاتنين يترفضوا مع بعض في نفس اللحظة.
 */
object MapsIntegrityCheck {

    private val SUSPICIOUS_LIBRARY_MARKERS = arrayOf(
        "frida-agent",
        "frida-gadget",
        "linjector",
        "xposed"
    )

    fun isMemoryTampered(): Boolean {
        return try {
            BufferedReader(FileReader("/proc/self/maps")).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val lower = line?.lowercase() ?: continue
                    if (SUSPICIOUS_LIBRARY_MARKERS.any { lower.contains(it) }) {
                        return true
                    }
                }
            }
            false
        } catch (e: Exception) {
            // لو فشل الفحص نفسه (مثلاً صلاحيات)، منعتبرهوش دليل قاطع بمفرده
            false
        }
    }
}
