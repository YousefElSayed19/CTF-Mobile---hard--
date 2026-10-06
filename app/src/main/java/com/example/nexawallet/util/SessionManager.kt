package com.example.nexawallet.util

/**
 * حالة الجلسة الحالية في الذاكرة (مش persisted عمدًا، عشان تبقى
 * "قيمة حية" في الذاكرة وقت التشغيل - دي القيمة التانية اللي
 * التحدي محتاج اللاعب يلاقيها ويتلاعب بيها مع الـ limit مع بعض).
 */
object SessionManager {

    var currentUsername: String? = null
        private set

    /**
     * session flag بسيط بيتحط true وقت تسجيل الدخول الناجح.
     * الاسم مقصود يكون غامض (sessionApproved) مش مرتبط مباشرة
     * بمعنى "صلاحية التحويل".
     */
    var sessionApproved: Boolean = false
        private set

    fun onLoginSuccess(username: String) {
        currentUsername = username
        sessionApproved = true
    }

    fun clear() {
        currentUsername = null
        sessionApproved = false
    }
}
