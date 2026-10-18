# Objective

## What this challenge is testing

NexaWallet is a mobile crackme built to exercise **dynamic** Android reverse engineering — the player is expected to use runtime instrumentation (Frida), not just a static decompile-and-patch approach, because:

- There is no secret string, hidden flag, or precomputed answer sitting in the APK for a plain `strings` scan or a quick jadx read to turn up directly — the flag constant is XOR+Base64-obfuscated, not plaintext, inside `TransferAuthorizer`.
- The flag is **fixed** (the same for every player, every attempt — a catalog requirement, since the lab ships a single `flag_hash`), but it is only ever produced by `TransferAuthorizer.mintClearanceToken()`, which only executes once the app’s own authorization logic decides a transfer is legitimately approved beyond the limit — reaching that call is the actual challenge, not the decode step itself.
- Getting there requires defeating two independent, compounding security mechanisms, each one testing a different skill.
- A sufficiently determined static reader who locates the obfuscated constant and its XOR key can, in principle, decode the flag offline without ever touching the live app. This is an intentional, documented trade-off (see `validation-report.mdx` in the maintainer docs) rather than an oversight: it mirrors the "effort, not prevention" principle behind every client-side protection in this lab, and the intended, expected solve path is still the full dynamic chain below.

## Skills exercised

1. **Static analysis of a modern Kotlin/Android APK** — using jadx to decompile and navigate the app’s package structure, reading `AndroidManifest.xml` to see what’s declared (and what’s conspicuously never launched from anywhere).
2. **Frida fundamentals** — attaching to a running or spawned process, `Java.perform`, `Java.use`, and overriding a method’s `.implementation`.
3. **Diagnosing a hook that “installs” but doesn’t actually fire** — recognizing the signs of ART/JIT method inlining defeating an application-level hook, and knowing to pivot to hooking the underlying **framework** methods (`java.net.Socket`, `java.io.BufferedReader`, `java.lang.Thread`) instead of the app’s own wrapper function.
4. **Understanding Android component visibility** — the difference between an `Activity` that’s merely present in the compiled code versus one that’s actually reachable through the app’s UI or through the OS’s own component-launch mechanism (`exported` in the manifest), and what it means for an attacker who already has code execution inside the target process.
5. **Driving arbitrary in-process behavior with Frida beyond simple hooking** — using `startActivity()` from a Frida script (via the app’s own `Context`) to reach and trigger a code path that the UI itself never exposes.
6. **Chaining the result into the app’s real business logic** — recognizing that defeating the detection layer alone is not sufficient; a second, independent authorization condition still has to be satisfied before the intended (over-limit) code path — and the flag it mints — becomes reachable.

## What is explicitly *not* being tested

- Native/JNI reverse engineering (the entire app is Kotlin; there is no `.so` library to disassemble).
- Cryptographic cryptanalysis — the flag token is a straightforward SHA-256-derived hex string; recovering it is about reaching the code path that computes it, not about breaking the hash.
- Network interception (SSL pinning layers exist in the codebase for realism but are not required to solve this particular challenge — there is no real backend to intercept).