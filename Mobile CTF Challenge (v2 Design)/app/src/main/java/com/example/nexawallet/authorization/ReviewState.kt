package com.example.nexawallet.authorization

import com.example.nexawallet.ui.ReviewGateActivity

/**
 * Gate 2 — clearance state for a transfer above the standard limit.
 *
 * Deliberately NOT a plain mutable boolean that can be flipped from
 * anywhere with a bare static setter. grantClearance() requires a
 * live ReviewGateActivity instance as proof that the call actually
 * originated from that screen's own flow — a reference to an
 * activity that has already finished or been destroyed does not
 * count, so a player can't just grab a stale/cached instance either.
 *
 * This state is intentionally never set anywhere in the normal,
 * reachable UI flow of the app. Nothing else in TransferActivity,
 * HomeActivity, etc. calls grantClearance() — the only legitimate
 * path to it is through ReviewGateActivity itself.
 */
object ReviewState {

    @Volatile
    private var cleared: Boolean = false

    val isCleared: Boolean
        get() = cleared

    fun grantClearance(from: ReviewGateActivity) {
        if (!from.isFinishing && !from.isDestroyed) {
            cleared = true
        }
    }

    fun reset() {
        cleared = false
    }
}
