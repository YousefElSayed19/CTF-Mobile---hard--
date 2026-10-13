package com.example.nexawallet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nexawallet.authorization.ReviewState
import com.example.nexawallet.databinding.ActivityReviewGateBinding

/**
 * Gate 2 — hidden screen that grants transfer clearance.
 *
 * Not reachable from any visible UI path: no other Activity in this
 * app ever launches it, and it is declared android:exported="false"
 * in the manifest, so it also can't be started externally via
 * `adb shell am start -n ...`. The only way to reach it is from
 * within the running process itself — e.g. a Frida script invoking
 * startActivity() using the app's own Context/Application instance.
 *
 * Reaching this screen is necessary but not sufficient on its own:
 * ReviewState.grantClearance() also checks that the Activity instance
 * passed to it is actually alive (not finishing/destroyed), so the
 * clearance has to come from a genuinely live instance of this screen.
 */
class ReviewGateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewGateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewGateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGrantClearance.setOnClickListener {
            ReviewState.grantClearance(this)
            binding.tvReviewStatus.text = "Clearance granted for this session."
        }
    }
}
