package com.example.autoclicker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.btnOverlay).setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            if (!isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "Turn on the Accessibility Service first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Grant the 'display over other apps' permission first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            startService(Intent(this, OverlayService::class.java))
            Toast.makeText(this, "Floating controller started. Switch to any app.", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val accOk = isAccessibilityServiceEnabled()
        val overlayOk = Settings.canDrawOverlays(this)
        statusText.text = buildString {
            append(if (accOk) "\u2714 Accessibility service enabled\n" else "\u2718 Accessibility service NOT enabled\n")
            append(if (overlayOk) "\u2714 Overlay permission granted" else "\u2718 Overlay permission NOT granted")
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expected = "$packageName/${ClickAccessibilityService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        if (TextUtils.isEmpty(enabledServices)) return false
        return enabledServices.split(":").any { it.equals(expected, ignoreCase = true) }
    }
}