package com.example.nexawallet.util


object SessionManager {

    var currentUsername: String? = null
        private set


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
