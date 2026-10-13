package com.example.nexawallet.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Simplified local stand-in for a "server" until a real backend is
 * wired up. All values here would come from an API in the final
 * version.
 */
object LocalLedger {

    private const val PREFS_NAME = "nexawallet_prefs"
    private const val KEY_USERS = "registered_users" // username:password pairs, semicolon-separated (illustrative only)
    private const val KEY_BALANCE_PREFIX = "balance_"
    private const val KEY_LIMIT_PREFIX = "limit_"

    private const val DEFAULT_BALANCE = 1500_00L   // in piastres: 1500.00 EGP
    private const val DEFAULT_MAX_LIMIT = 100_00L  // 100.00 EGP max transfer

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun register(context: Context, username: String, password: String): Boolean {
        val p = prefs(context)
        val existing = p.getString(KEY_USERS, "") ?: ""
        if (existing.contains("$username:")) return false // already exists

        val updated = "$existing$username:$password;"
        p.edit()
            .putString(KEY_USERS, updated)
            .putLong(KEY_BALANCE_PREFIX + username, DEFAULT_BALANCE)
            .putLong(KEY_LIMIT_PREFIX + username, DEFAULT_MAX_LIMIT)
            .apply()
        return true
    }

    fun login(context: Context, username: String, password: String): Boolean {
        val p = prefs(context)
        val existing = p.getString(KEY_USERS, "") ?: ""
        return existing.contains("$username:$password;")
    }

    fun getBalance(context: Context, username: String): Long =
        prefs(context).getLong(KEY_BALANCE_PREFIX + username, DEFAULT_BALANCE)

    fun getMaxTransferLimit(context: Context, username: String): Long =
        prefs(context).getLong(KEY_LIMIT_PREFIX + username, DEFAULT_MAX_LIMIT)

    fun deductBalance(context: Context, username: String, amount: Long) {
        val p = prefs(context)
        val current = getBalance(context, username)
        p.edit().putLong(KEY_BALANCE_PREFIX + username, current - amount).apply()
    }
}
