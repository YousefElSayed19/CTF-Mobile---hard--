# NexaWallet — Mobile CTF Challenge (Hard)

An Android application designed as a CTF challenge that relies entirely on **dynamic instrumentation** (Frida) rather than static reverse engineering.

The scenario is a fictional digital wallet with a maximum transfer limit. The player's goal is to bypass this limit by manipulating the application's behavior at runtime.

> ⚠️ This is an educational project for CTF/security training purposes only. The names and branding (NexaWallet) are completely fictional and have no relation to any real application or bank.

---

## Current Project Status

- **There is no real backend yet.** All data (users, balance, maximum transfer limit) is currently stored locally in `SharedPreferences` through `MockBackend.kt`, until a real server is built and integrated later.

- The SSL Pinning implementation (in `PinningConfig.kt` and `WebViewTrustManager.kt`) is fully implemented but currently uses placeholder values (fake pins) because there is no real server to connect to yet. These values will need to be updated once the backend is added.

- The "flag" generation in `TransferActivity.generateBypassToken()` is currently local and temporary. In the final version, it would be better for the server to verify that the transfer was actually bypassed and return the real flag instead of generating it locally inside the application.

---

## Project Structure

```text
app/src/main/java/com/example/nexawallet/

├── ui/
│   ├── LoginActivity.kt        # Login
│   ├── RegisterActivity.kt     # Simple account registration
│   ├── HomeActivity.kt         # Displays balance + maximum transfer limit
│   ├── TransferActivity.kt     # ★ Main challenge screen (vulnerability here)
│   ├── WebTransferActivity.kt  # WebView screen (hosts the second SSL pinning layer)
│   └── BlockedActivity.kt      # Block screen if tampering is detected
│
├── security/
│   ├── RootFridaDetector.kt    # First root/Frida detection point (file + port checks)
│   └── MapsIntegrityCheck.kt   # Second independent detection point (/proc/self/maps)
│
├── network/
│   ├── PinningConfig.kt        # SSL Pinning - standard OkHttp layer
│   └── WebViewTrustManager.kt  # SSL Pinning - separate WebView layer
│
└── util/
    ├── MockBackend.kt           # Local mock backend (temporary)
    └── SessionManager.kt        # In-memory session state (sessionApproved)
```

---

## Expected Player Journey

1. Open the application, create an account through Register, and log in.

2. Check the home screen to see the maximum transfer limit.

3. Navigate to the Transfer screen and attempt to transfer an amount greater than the allowed limit → the transfer is rejected.

4. Investigate the rejection logic using dynamic analysis tools (Frida) rather than relying only on static code analysis.

5. Discover that there are two independent root/Frida detection mechanisms (`RootFridaDetector` and `MapsIntegrityCheck`) that both need to be bypassed.

6. Discover that the actual decision is made inside `validateOp()` in `TransferActivity`, which depends on two values: the maximum transfer limit and the session state.

7. Create an appropriate hook that allows the operation to succeed despite exceeding the transfer limit.

8. Obtain the token/flag displayed on the screen.

---

## Build Instructions

The project requires **Android Studio** and cannot be built without the Android SDK.

### 1. Open the Project

Open the project in Android Studio:

```text
File → Open → Select the NexaWallet folder
```

### 2. Gradle Sync

Let Android Studio perform the Gradle Sync automatically.

On the first run, it may need to download the Gradle wrapper and dependencies such as OkHttp/AndroidX, so an internet connection is required.

### 3. Run the Application

Run the application on a **rooted emulator** or a **rooted physical Android device** so that Frida can be used.

---

## Important Notes Before Final Submission

- Replace the placeholder values in `PinningConfig.kt` and `WebViewTrustManager.kt` with the real SSL pinning values once the actual backend server is integrated.

- The package name (`com.example.nexawallet`) should preferably be changed to a custom package name for the competition/challenge before distribution.

- Consider enabling ProGuard/R8 (`minifyEnabled`) in the release build if you want to increase the difficulty of any static analysis that may happen accidentally.

---

## Remaining Tasks

The following items are **not part of this submission**:

- [ ] Build a real backend with the following endpoints:

  ```text
  register / login / balance / transfer / verify-flag
  ```

- [ ] Connect the real SSL pinning values to the actual backend certificate.

- [ ] Perform a complete internal test of the challenge by solving it from scratch using Frida to verify the actual difficulty level.

- [ ] Write an official solution/write-up for the organizers.
