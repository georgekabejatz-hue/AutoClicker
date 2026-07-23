package com.example.autoclicker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val points = mutableListOf<ClickPoint>()
    private var isRunning = false
    private var intervalMs = 1000L
    private var pointIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    private val tapLoop = object : Runnable {
        override fun run() {
            if (!isRunning || points.isEmpty()) return
            val point = points[pointIndex % points.size]
            ClickAccessibilityService.instance?.tap(point.x, point.y)
            pointIndex++
            handler.postDelayed(this, intervalMs)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showOverlay()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "auto_clicker_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Auto Clicker", NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Auto Clicker running")
            .setContentText("Floating controller active")
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .build()
        startForeground(1, notification)
    }

    private fun showOverlay() {
        val textView = TextView(this)
        textView.text = "🎯 Auto Clicker Active\nTap to add point"
        textView.setBackgroundColor(0xCC000000.toInt())
        textView.setTextColor(0xFFFFFFFF.toInt())
        textView.setPadding(20, 20, 20, 20)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else 
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 40
        params.y = 120

        textView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    points.add(ClickPoint(event.rawX, event.rawY))
                    Toast.makeText(this, "Point ${points.size} added", Toast.LENGTH_SHORT).show()

                    if (!isRunning) {
                        isRunning = true
                        pointIndex = 0
                        handler.post(tapLoop)
                        Toast.makeText(this, "Auto-clicking started!", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        overlayView = textView
        windowManager.addView(textView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tapLoop)
        overlayView?.let { 
            runCatching { windowManager.removeView(it) }
        }
    }
}