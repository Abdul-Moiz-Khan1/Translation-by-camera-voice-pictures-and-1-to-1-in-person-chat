package com.example.frontend.presentation.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.frontend.R

class FloatingButtonService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: View

    private lateinit var popupView: View
    private var isPopupAttached = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("overlay", "serviceCreated")
        startForegroundService()
        setupFloatingButton()
    }

    private fun startForegroundService() {
        Log.d("overlay", "forgoundService")
        val notification = NotificationCompat.Builder(this, "floating_button_channel")
            .setContentTitle("Floating Button Service")
            .setContentText("Displaying floating button")
            .setSmallIcon(R.drawable.floating_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
    }

    private fun setupFloatingButton() {

        Log.d("overlay", "setupFloatingbtn")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatingButton = LayoutInflater.from(this).inflate(R.layout.floating_icon_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }

        // Make the button draggable
        floatingButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0f
            private var initialTouchY: Float = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingButton, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        return true
                    }
                }
                return false
            }
        })

        floatingButton.setOnClickListener {
           setupClickHandler()
                  }

        windowManager.addView(floatingButton, params)
    }


    private fun setupClickHandler() {
        floatingButton.setOnClickListener {
            togglePopup()
        }
    }

    private fun togglePopup() {
        if (::popupView.isInitialized && isPopupAttached) {
            windowManager.removeView(popupView)
            isPopupAttached = false
        } else {
            showPopup()
        }
    }

    private fun showPopup() {
        popupView = LayoutInflater.from(this).inflate(
            R.layout.popup_view,
            null
        )

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = floatingButton.x.toInt()
            y = floatingButton.y.toInt() + floatingButton.height
        }

        // Setup translate button click
        popupView.findViewById<Button>(R.id.translateScreenBtn).setOnClickListener {
            translateText()
        }
        popupView.findViewById<ImageView>(R.id.backbtn_popup).setOnClickListener {
            windowManager.removeView(popupView)
            isPopupAttached = false
        }

        windowManager.addView(popupView, params)
        isPopupAttached = true

        // Auto-close when touching outside
        popupView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                windowManager.removeView(popupView)
                isPopupAttached = false
            }
            true
        }
    }

    private fun translateText() {
        val textView = popupView.findViewById<TextView>(R.id.popupText)
        val currentText = textView.text.toString()

        // Simple translation example - replace with your actual translation logic
        val translatedText = when (currentText) {
            "Hello! Click translate below" -> "¡Hola! Haz clic en traducir abajo"
            "¡Hola! Haz clic en traducir abajo" -> "Bonjour! Cliquez sur traduire ci-dessous"
            else -> "Hello! Click translate below"
        }

        textView.text = translatedText
        Toast.makeText(this, "Text translated", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPopupAttached && ::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }
        if (isPopupAttached && ::popupView.isInitialized) {
            windowManager.removeView(popupView)
        }
    }
}