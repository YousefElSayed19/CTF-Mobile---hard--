package com.example.nexawallet.session

/**
 * Login/session state only — who's logged in, nothing else.
 *
 * Unlike v1's SessionManager, this object has no "approved" flag of
 * any kind. Authorization for transfers above the limit lives
 * entirely in ReviewState (authorization package), kept deliberately
 * separate so that logging in never implies any transfer clearance.
 */
object UserSession {

    var currentUsername: String? = null
        private set

    fun onLoginSuccess(username: String) {
        currentUsername = username
    }

    fun clear() {
        currentUsername = null
    }
}
