# NexaWallet — Mobile CTF Challenge (v2 Design)

An Android application designed as a CTF challenge that relies entirely on **dynamic instrumentation** (Frida) rather than static reverse engineering.

The scenario is a fictional digital wallet with a maximum transfer limit. The player's goal is to bypass this limit by manipulating the application's behavior at runtime.

> ⚠️ This is an educational project for CTF/security training purposes only. The names and branding (NexaWallet) are completely fictional and have no relation to any real application or bank.

> This is the **v2 design**, replacing the original (v1) implementation. See `../Mobile CTF Challenge (v1 Design)/README.md` and `../Mobile CTF Challenge (v1 Design)/OLD_DESIGN_NOTES.md` for the previous version and the rationale behind the redesign. This document describes the **target design** for v2 — implementation is in progress.

---

## Design Goals

The v2 redesign has three explicit goals, in order of priority:

1. **Zero environment-dependent false positives.** Every detection check must be deterministic — triggered only by the player actually running an analysis tool (Frida), never by emulator/root state, OS image, or device configuration.
2. **Exactly two gates to bypass**, not four scattered ones. Fewer checkpoints, but each one is deeper and requires real understanding to defeat.
3. **Diversified skillset.** The challenge should not be solvable through Frida hooking alone — it should also require reading and understanding the app's structure (manifest, activity graph, call context) to find the full solution path.

---

## The Two Gates

### Gate 1 — Integrity Gate (instrumentation detection)

A single, unified detection entry point that aggregates multiple deterministic signals of dynamic instrumentation (Frida) being active. No file-existence checks, no root checks — nothing that depends on the state of the device itself.

### Gate 2 — Review Clearance (authorization layer)

A transfer above the stated limit is not simply rejected — it requires a separate "clearance" state to be granted. This state is **not** set by any visible, reachable UI path in the normal app flow. It lives behind a hidden screen that:

- is not launched from anywhere in the normal navigation graph,
- is not exported in the manifest (cannot be opened externally via `adb shell am start`),
- validates that the call granting clearance actually originates from a live instance of that screen (not a bare static setter that can be called from anywhere with no context).

A transfer only succeeds if **both** gates are satisfied together — passing the integrity gate alone is not enough, and vice versa.

---

## Project Structure (Target)

```text
app/src/main/java/com/example/nexawallet/

├── ui/
│   ├── LoginActivity.kt          # Login
│   ├── RegisterActivity.kt       # Simple account registration
│   ├── HomeActivity.kt           # Displays balance + maximum transfer limit
│   ├── TransferActivity.kt       # ★ Main challenge screen
│   ├── WebTransferActivity.kt    # WebView screen (hosts SSL pinning layer)
│   ├── RestrictedActivity.kt     # Shown when the Integrity Gate fails
│   └── ReviewGateActivity.kt     # Hidden screen — grants Review Clearance (Gate 2)
│
├── integrity/
│   └── IntegrityGate.kt          # Single unified Frida-detection entry point (Gate 1)
│
├── authorization/
│   ├── TransferAuthorizer.kt     # Decides if a transfer amount is allowed (limit + clearance)
│   └── ReviewState.kt            # Holds clearance state, only settable from a valid Activity context
│
├── session/
│   └── UserSession.kt            # Login/session state only (username, logged-in flag)
│
├── network/
│   ├── PinningConfig.kt          # SSL Pinning — standard OkHttp layer
│   └── WebViewTrustManager.kt    # SSL Pinning — separate WebView layer
│
└── data/
    └── LocalLedger.kt            # Local mock backend (balance, limit, accounts)
```

### Naming changes from v1

| v1 (legacy)                          | v2 (new)                              |
|---------------------------------------|----------------------------------------|
| `RootFridaDetector` + `MapsIntegrityCheck` | `IntegrityGate` (single, merged)   |
| `SessionManager.sessionApproved`      | `UserSession` (login state only)      |
| `SessionManager.adminTransferApproved`| `ReviewState` (own isolated object)   |
| `TransferActivity.validateOp()`       | `TransferAuthorizer.isTransferAllowed()` |
| `BlockedActivity`                     | `RestrictedActivity`                  |
| `MockBackend`                         | `LocalLedger`                         |
| `TransferActivity.generateBypassToken()` | `TransferAuthorizer.mintClearanceToken()` |

---

## Expected Player Journey

1. Open the application, register, and log in.
2. Check the home screen for the maximum transfer limit.
3. Attempt a transfer above the limit on the Transfer screen → rejected.
4. Investigate with Frida, discover `IntegrityGate` and hook its detection signals (all deterministic — no environment-dependent false positives to fight).
5. Reach `TransferActivity`/`TransferAuthorizer` and discover the rejection depends on **two** independent conditions: the numeric limit check, and a `ReviewState` clearance flag.
6. Discover that `ReviewState` is never set anywhere in the reachable UI flow — requires digging through the manifest/activity graph to find `ReviewGateActivity`.
7. Discover `ReviewGateActivity` is not exported and not reachable via any visible button — requires invoking it from within the running process (e.g. via Frida calling `startActivity` using the app's own context), or finding another legitimate in-process trigger.
8. Grant clearance through the discovered path, return to the transfer flow, and complete a transfer above the limit.
9. Obtain the token/flag displayed on screen.

---

## Implementation Status

This document describes the **target design**. Current status:

- [ ] `IntegrityGate.kt` — not yet implemented
- [ ] `ReviewGateActivity.kt` + manifest entry (non-exported) — not yet implemented
- [ ] `ReviewState.kt` — not yet implemented
- [ ] `TransferAuthorizer.kt` — not yet implemented
- [ ] `RestrictedActivity.kt` (rename from `BlockedActivity`) — not yet implemented
- [ ] `UserSession.kt` (rename from `SessionManager`, approval state removed) — not yet implemented
- [ ] `LocalLedger.kt` (rename from `MockBackend`) — not yet implemented
- [ ] Internal solve-test using Frida to confirm actual difficulty and that the challenge remains solvable end-to-end
- [ ] Official write-up / solution document for organizers

---

## Build Instructions

The project requires **Android Studio** and cannot be built without the Android SDK.

### 1. Open the Project
```text
File → Open → Select the NexaWallet folder
```

### 2. Gradle Sync
Let Android Studio perform the Gradle Sync automatically. On first run it needs to download the Gradle wrapper and dependencies (OkHttp/AndroidX), so an internet connection is required.

### 3. Run the Application
Run on an emulator or physical device with Frida available for dynamic analysis. No root is required by the detection logic itself in v2 — any standard emulator image works.

---

## Notes Carried Over from v1

- SSL Pinning (`PinningConfig.kt`, `WebViewTrustManager.kt`) still uses placeholder pin values until a real backend is integrated.
- The package name (`com.example.nexawallet`) should be changed to a custom name before public distribution.
- Consider enabling ProGuard/R8 (`minifyEnabled`) in the release build to raise the bar against incidental static analysis.
- A real backend (register/login/balance/transfer/verify-flag endpoints) is still planned but not required for the core challenge logic.
