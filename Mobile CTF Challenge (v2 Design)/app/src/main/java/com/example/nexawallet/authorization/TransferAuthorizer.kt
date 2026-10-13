package com.example.nexawallet.authorization

import java.security.MessageDigest

/**
 * Decides whether a transfer is allowed, and mints the flag token
 * once it legitimately succeeds above the standard limit.
 *
 * A transfer is allowed if either:
 *  - the requested amount is within the standard limit (the normal,
 *    everyday case — no review needed), or
 *  - Gate 2 (ReviewState) has been cleared through its own flow.
 *
 * Both gates are independent: bypassing IntegrityGate alone does not
 * touch this logic at all, and reaching ReviewState.isCleared without
 * also getting past IntegrityGate never happens in practice, since
 * TransferActivity checks IntegrityGate before this screen is even
 * reachable.
 */
object TransferAuthorizer {

    fun isTransferAllowed(requestedAmount: Long, limit: Long): Boolean {
        return requestedAmount <= limit || ReviewState.isCleared
    }

    fun mintClearanceToken(username: String, amount: Long): String {
        val raw = "$username:$amount:${System.currentTimeMillis() / 100_000}:nexawallet-review-secret"
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        val hex = digest.joinToString("") { "%02x".format(it) }.take(32)
        return "duck{$hex}"
    }
}
