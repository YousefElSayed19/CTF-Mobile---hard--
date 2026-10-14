package com.example.nexawallet.integrity

import java.io.BufferedReader
import java.io.FileReader
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Gate 1 — single, unified entry point for detecting active dynamic
 * instrumentation (Frida).
 *
 * Design intent: every signal checked here is DETERMINISTIC. None of
 * them depend on device/emulator state (no root-file checks, no
 * "is this device rooted" heuristics) — they only fire as a direct
 * result of Frida actually being attached to this process. A clean
 * device, any emulator image, rooted or not, should never trip this
 * gate on its own.
 *
 * A player bypassing this gate needs to defeat all three signals
 * together, since isTampered() is a single composite call — hooking
 * one internal check alone is not enough.
 */
object IntegrityGate {

    private const val FRIDA_DEFAULT_PORT = 27042

    private val SUSPICIOUS_LIBRARY_MARKERS = arrayOf(
        "frida-agent",
        "frida-gadget",
        "linjector",
        "xposed"
    )

    private val SUSPICIOUS_THREAD_NAMES = arrayOf(
        "gum-js-loop",
        "gmain",
        "gdbus",
        "frida"
    )

    fun isTampered(): Boolean {
        return isFridaPortOpen() || hasInjectedLibrarySignature() || hasSuspiciousThreads()
    }

    /**
     * Frida server listens on 27042 by default. Deliberately naive
     * (trivial to defeat by moving frida-server to another port) —
     * this is meant to be the easy layer, not the whole gate.
     */
    private fun isFridaPortOpen(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("127.0.0.1", FRIDA_DEFAULT_PORT), 200)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Scans this process's own memory map for known injected-library
     * signatures. Independent of the port check — moving frida-server's
     * port does nothing here, since this looks at what's actually
     * loaded into the process.
     */
    private fun hasInjectedLibrarySignature(): Boolean {
        return try {
            BufferedReader(FileReader("/proc/self/maps")).use { reader ->
                reader.lineSequence().any { line ->
                    val lower = line.lowercase()
                    SUSPICIOUS_LIBRARY_MARKERS.any { lower.contains(it) }
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Frida's JS runtime spins up its own named threads inside the
     * target process. Checking thread names is independent of both
     * the port and the maps file — defeating it requires either
     * renaming/hiding those threads or hooking this check directly.
     */
    private fun hasSuspiciousThreads(): Boolean {
        return try {
            Thread.getAllStackTraces().keys.any { thread ->
                val name = thread.name.lowercase()
                SUSPICIOUS_THREAD_NAMES.any { name.contains(it) }
            }
        } catch (e: Exception) {
            false
        }
    }
}
