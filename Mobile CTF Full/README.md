# Mobile Reverse Engineering CTF — Task ## / Challenge #1

**Participant:** yousef elsayed
**Domain:** Mobile Application Security / Reverse Engineering (Android)
**Difficulty:** Hard
**Flag:** `duck{...}`

## What this is

A custom-built Android crackme (**NexaWallet**), designed and built from scratch for this task, then solved end-to-end to validate it and produce the evidence below.

The app simulates a digital wallet with a maximum transfer limit. The player's goal is to get the app to approve and confirm a money transfer **above** that limit, using dynamic instrumentation (Frida) rather than static patching — the app actively detects and blocks common tampering/analysis attempts.

## Repo layout

```
.
│
├── bin/                        ← the installable, playable challenge file
│   └── NexaWallet-debug.apk    (placeholder — see bin/README.md)
│
├── documentation/              ← the written evidence/report
│   ├── Objective.md
│   ├── Public Briefing.md
│   ├── Walkthrough.md
│   ├── What Happened and Why (Root Cause).md
│   ├── Mitigation.md
│   └── images/                 ← screenshots referenced from Walkthrough.md
│
└── src/                         ← pointer to the full source (private — never ship to participants)
    ├── BUILD_INSTRUCTIONS.md   ← how to open/build the APK from source
    └── README.md               ← where the actual Android Studio project lives in this repo
```

## Where to start

| If you want to... | Go to |
|---|---|
| Understand what the challenge is testing | `documentation/Objective.md` |
| See the player-facing description (no spoilers) | `documentation/Public Briefing.md` |
| Reproduce the full solve, step by step | `documentation/Walkthrough.md` |
| Understand the security root cause being demonstrated | `documentation/What Happened and Why (Root Cause).md` |
| See mitigation recommendations for real-world systems | `documentation/Mitigation.md` |
| Rebuild the APK from source | `src/BUILD_INSTRUCTIONS.md` |

## Design summary

NexaWallet is a single Android app (Kotlin, View Binding, OkHttp). There is no real backend — balances and limits are stored locally for the purposes of this challenge — so the entire security boundary the player must defeat lives **inside the APK itself**, enforced at runtime.

Two independent gates stand between the player and the flag:

1. **Integrity Gate** — a unified, fully deterministic check for active dynamic instrumentation (Frida): a loopback port probe, a scan of the process's own memory map for injected-library signatures, and a scan of live thread names for Frida's own runtime threads. Any one of the three tripping blocks the player with a "Security Check Failed" screen. Hooking the app's own top-level check function directly does **not** work here — see the Walkthrough and Root Cause docs for why, and what the player has to pivot to instead.
2. **Review Clearance** — even after Gate 1 is defeated, a transfer above the limit still requires a separate, independent "clearance" flag to be set. That flag is granted only by a screen that is never reachable from any button, menu, or link in the app's normal flow, and is declared non-exported in the manifest, so it can't be opened externally either. The player has to discover it exists by reading the decompiled code/manifest, then trigger it from inside the running process.

Only when **both** gates are satisfied does the app mint and display the flag — nothing is ever hidden as a plain string in the APK.

## Evidence status

- [x] Challenge designed and built (Kotlin/Android, Gradle/AGP, View Binding)
- [x] Full solve performed and verified (jadx static analysis + Frida dynamic instrumentation)
- [x] Flag confirmed reproducible end-to-end on a clean emulator image
- [x] No environment-dependent false positives (confirmed: a clean device with no Frida attached always reaches the Transfer screen and is only limited by the normal EGP 100 cap, never blocked)
- [x] Full Walkthrough, Objective, Public Briefing, Root Cause and Mitigation documentation completed
