# Mitigation

# Mitigation — Recommendations for Real-World Systems

These map directly onto the four root-cause points. They describe how a *real* mobile banking/wallet system should be built to avoid the class of weakness this challenge demonstrates — not changes to make this specific crackme harder (it’s intentionally built to be beatable).

## 1. Never make the authorization decision client-side

The single highest-impact fix: a mobile app should never be the system of record for “is this transfer allowed.” The app should only **submit a request** (amount, account, intended recipient); a backend service — one the attacker does not control — must independently re-validate the amount against the account’s actual limit, risk score, and any required review workflow, and only the backend’s response determines whether the transfer proceeds. If the backend is unreachable, the safe default is to decline, never to fall back to a client-computed answer.

## 2. Treat instrumentation/root/tamper detection as a signal, not a gate

Checks like `IntegrityGate` (root detection, Frida/Xposed detection, debugger detection) are legitimate and worth having — but their only safe role is to be reported to the backend as one more risk signal (alongside device attestation, behavioral biometrics, velocity checks, etc.) that the backend factors into its own decision. Treating a client-side “not tampered” result as suficient permission to proceed with a sensitive action is the mistake — the check can be true and still be wrong, because the attacker controls the process making the check. For real device-integrity assurance, prefer a platform attestation service whose result is cryptographically signed by a party the client cannot forge (e.g. Android’s Play Integrity API), and verify that signed result **on the backend**, not just read a boolean inside the app.

## 3. Any “manual review / approval” workflow must be real, server-side, and signed

If a product genuinely wants a human-in-the-loop override for above-limit transfers (a legitimate feature design, unlike this challenge’s fictional version of it), the approval must be a server-side event: a real reviewer acting in a backend admin tool, which — once approved — issues a short-lived, cryptographically signed authorization token tied to that specific transaction (amount, account, timestamp, nonce) that the backend itself later verifies before executing the transfer. A boolean living only in the mobile app’s process memory, settable by any code running inside that process, is never a substitute for this, no matter how many checks are layered in front of setting it.

## 4. Never let the client mint its own proof of success

Any value that represents “this privileged action was authorized and completed” (a transaction confirmation, a receipt, a flag/token) must be generated and signed by the backend after it independently verifies the action, and simply relayed to the client for display. If the client can compute that value on its own — even with a complex formula, obfuscated constants, or encryption — then reverse-engineering the APK is sufficient to produce it without the backend ever being involved, which defeats the entire point of it being proof of anything.

## Supporting measures (worth doing, but not a substitute for 1–4)

- **SSL/certificate pinning** (already present in this app’s design via `PinningConfig` / `WebViewTrustManager`): protects the confidentiality and integrity of client↔︎server traffic, which matters once there *is* a real backend making the actual decision — but pinning a connection to a server that isn’t the one making the authorization decision doesn’t help.
- **Code obfuscation / hardening** (R8/ProGuard full mode, native-code checks, anti-debugging): raises the time and skill cost of static and dynamic analysis. Useful as defense-in-depth, but should be budgeted as a *delay*, not a *control* — assume a sufficiently motivated attacker with full device control (their own phone, their own rules) eventually gets through anything that lives entirely client-side.
- **Server-side rate limiting and anomaly detection** on the transfer endpoint itself, independent of whatever the client reports about its own integrity — catches abuse even if every client-side check above is somehow defeated.