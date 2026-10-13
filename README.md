# NexaWallet — Mobile CTF Challenge

This repository contains **two versions** of the same CTF challenge:

| Folder | Status | Description |
|---|---|---|
| [`Mobile CTF Challenge (v1 Design)`](./Mobile%20CTF%20Challenge%20(v1%20Design)/) | Legacy — superseded | The original implementation. Kept for historical reference only. |
| [`Mobile CTF Challenge (v2 Design)`](./Mobile%20CTF%20Challenge%20(v2%20Design)/) | Active — current design | The redesigned challenge. This is the one being implemented and used going forward. |

## Why two versions!

The v1 design had a detection check that depended on the **emulator/device environment** rather than on the player actually using an analysis tool. In practice, this meant some players were blocked with a "Tampering Detected" error without ever touching Frida — just because of which emulator image they happened to be using. That made the challenge inconsistent and wasted player time on an environment issue instead of the actual challenge.

Rather than patching that one check, the detection and authorization logic was redesigned from scratch in v2: every check is now deterministic (triggered only by real Frida usage), the number of checkpoints was reduced and deepened, and a new layer (hidden-screen / app-structure exploration) was added to diversify the skills the challenge requires.

See `Mobile CTF Challenge (v1 Design)/OLD_DESIGN_NOTES.md` for the full technical rationale.

**If you're here to play or review the challenge, go to `Mobile CTF Challenge (v2 Design)` — that's the current, active version.**

> **Note (Windows users):** cloning may fail with `Filename too long` due to deep build-artifact paths under v1. Run `git config --global core.longpaths true`, enable Windows long paths (`LongPathsEnabled=1` under `HKLM\SYSTEM\CurrentControlSet\Control\FileSystem`, admin + reboot), then clone again.
