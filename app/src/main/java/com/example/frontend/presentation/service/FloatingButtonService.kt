package com.example.frontend.presentation.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.frontend.R
import com.example.frontend.presentation.view.ScreenCapturePermissionActivity
import com.example.frontend.presentation.view.ScreenCapturePermissionStore
import com.example.frontend.presentation.viewModel.HomeViewModel
import com.example.frontend.presentation.viewModel.OCRViewModel

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
            startForeground(
                1, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
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

        popupView.findViewById<Button>(R.id.translateScreenBtn).setOnClickListener {
            translateText()
        }
        popupView.findViewById<ImageView>(R.id.backbtn_popup).setOnClickListener {
            windowManager.removeView(popupView)
            isPopupAttached = false
        }

        windowManager.addView(popupView, params)
        isPopupAttached = true

        popupView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                windowManager.removeView(popupView)
                isPopupAttached = false
            }
            true
        }
    }

    private fun translateText() {

        val resultCode = ScreenCapturePermissionStore.resultCode
        val dataIntent = ScreenCapturePermissionStore.dataIntent

        if (resultCode != Activity.RESULT_OK || dataIntent == null) {
            val permissionIntent =
                Intent(applicationContext, ScreenCapturePermissionActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            startActivity(permissionIntent)
            return
        }

        val screenshotIntent = Intent(applicationContext, MyScreenshotService::class.java).apply {
            putExtra("code", resultCode)
            putExtra("data", dataIntent)
        }
        ContextCompat.startForegroundService(applicationContext, screenshotIntent)

        val resultTextView = popupView.findViewById<TextView>(R.id.popupText)

        val translatedObserver = Observer<String> { translated ->
            resultTextView.text = translated
            Log.d("FloatingService", "Translated: $translated")
        }
        OCRViewModelHolder.viewModel.translatedText.observeForever(translatedObserver)

        Handler(Looper.getMainLooper()).postDelayed({
            OCRViewModelHolder.viewModel.recognizedText.removeObserver(translatedObserver)
        }, 5000)


    }

    override fun onDestroy() {
        super.onDestroy()

        if (::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }

        if (::popupView.isInitialized) {
            windowManager.removeView(popupView)
        }
    }

}

object OCRViewModelHolder {
    val viewModel: OCRViewModel by lazy {
        OCRViewModel()
    }
}