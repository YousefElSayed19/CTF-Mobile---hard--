package com.example.nexawallet.authorization

import android.util.Base64

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

    // XOR(0x5A) + Base64 encoding of the fixed flag. Not a secret from
    // a sufficiently determined static reader (nothing embedded in a
    // client-side artifact ever is — see Mitigation.md point 4), but it
    // defeats a plain `strings`/quick-jadx-read scan, consistent with
    // this lab's design goal that no step be solvable by guessing or
    // a trivial text dump. The flag is intentionally fixed (does not
    // depend on username, amount, or wall-clock time) because this lab
    // ships a single flag_hash in lab.yml, a catalog requirement — see
    // validation-report.mdx for why an earlier, session-derived version
    // of this function had to change.
    private const val ENCODED_FLAG =
        "Pi85MSErbGIpBTs/MihpbmsoOCM0ajk5Ym08NjEoOztqMjQ3Iyc="
    private const val XOR_KEY = 0x5A

    fun isTransferAllowed(requestedAmount: Long, limit: Long): Boolean {
        return requestedAmount <= limit || ReviewState.isCleared
    }

    fun mintClearanceToken(username: String, amount: Long): String {
        val decoded = Base64.decode(ENCODED_FLAG, Base64.DEFAULT)
        val bytes = ByteArray(decoded.size) { i -> (decoded[i].toInt() xor XOR_KEY).toByte() }
        return String(bytes, Charsets.UTF_8)
    }
}
