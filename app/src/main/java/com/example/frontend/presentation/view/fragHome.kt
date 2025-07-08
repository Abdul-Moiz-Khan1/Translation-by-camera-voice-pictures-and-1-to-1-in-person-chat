package com.example.frontend.presentation.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.frontend.R
import com.example.frontend.databinding.FragmentFargCameraBinding
import com.example.frontend.databinding.FragmentFragHomeBinding
import com.example.frontend.presentation.service.FloatingButtonService
import com.example.frontend.presentation.service.MyScreenshotService
import com.example.frontend.presentation.service.OCRViewModelHolder
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class fragHome : Fragment() {
    private val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    private var _binding: FragmentFragHomeBinding? = null
    private val binding get() = _binding!!

    private val screenCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {

            ScreenCapturePermissionStore.resultCode = result.resultCode
            ScreenCapturePermissionStore.dataIntent = result.data
            Log.d("overlay", "result code ${result.resultCode} result data ${result.data}")

            val intent = Intent(requireContext(), FloatingButtonService::class.java)
            ContextCompat.startForegroundService(requireContext(), intent)
        } else {
            Toast.makeText(requireContext(), "Screen capture permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    binding.originalTextFragHome.setText(matches[0])
                }
            }
        }
    private var isPowerOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNotificationChannel()
        binding.mic1HomeFrag.setOnClickListener {
            startSpeechToText()
        }


        binding.translateBtnHomeFrag.setOnClickListener {
            val intent = Intent(requireContext(), Translation::class.java)
            intent.putExtra("content", binding.originalTextFragHome.text.toString())
            intent.putExtra(
                "targetLang",
                view.findViewById<TextView>(R.id.toLanguageText).text.toString()
            )
            startActivity(intent)
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

        binding.originalTextFragHome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                if (p0.toString().isNotEmpty()) {
                    binding.mic1HomeFrag.visibility = View.INVISIBLE
                    binding.translateBtnHomeFrag.visibility = View.VISIBLE
                } else {
                    binding.mic1HomeFrag.visibility = View.VISIBLE
                    binding.translateBtnHomeFrag.visibility = View.INVISIBLE
                }

            }

            override fun afterTextChanged(p0: Editable?) {


            }
        })

        binding.toggleButton.setOnClickListener {
            isPowerOn = !isPowerOn
            if (isPowerOn) {
                val projectionManager =
                    requireContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val intent = projectionManager.createScreenCaptureIntent()
                screenCaptureLauncher.launch(intent)
                binding.toggleButton.setBackgroundResource(R.drawable.bg_toggle_on)
                binding.powerText.text = "ON"
                binding.powerIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.appBlue
                    )
                )
                binding.toggleButton.rotation = 180f
                binding.powerIcon.rotation = 180f
                binding.powerText.rotation = 180f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1002
                    )
                }
                Log.d("overlay", "button turned On")
                checkOverlayPermission()

            } else {
                binding.toggleButton.setBackgroundResource(R.drawable.bg_togglwe)
                binding.powerText.text = "OFF"
                binding.powerIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.appOrange
                    )
                )
                binding.toggleButton.rotation = 0f
                binding.powerIcon.rotation = 0f
                binding.powerText.rotation = 0f

                val floatIntent = Intent(requireContext(), FloatingButtonService::class.java)
                requireContext().stopService(floatIntent)

                val screenshotIntent = Intent(requireContext(), MyScreenshotService::class.java)
                requireContext().stopService(screenshotIntent)

                OCRViewModelHolder.viewModel.reset()
            }
        }


        view.findViewById<LinearLayout>(R.id.to_lanugage_home).setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        val langCode = "en-US"
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.d("ERRORRR", e.message.toString())
        }

    }


    private fun checkOverlayPermission() {

        Log.d("overlay", "inCheckOverlay")
        if (!android.provider.Settings.canDrawOverlays(requireContext())) {
            AlertDialog.Builder(requireContext())
                .setTitle("Overlay Permission Needed")
                .setMessage("This feature requires the 'Display over other apps' permission")
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${requireContext().packageName}")
                    )
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {

            Log.d("overlay", "overlayPermissionGranted")
            startFloatingButtonService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (android.provider.Settings.canDrawOverlays(requireContext())) {
                    showFloatingButton()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showFloatingButton() {
        val serviceIntent = Intent(requireContext(), FloatingButtonService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    private fun startFloatingButtonService() {

        Log.d("overlay", "inStartFloatingButtonService")
        val serviceIntent = Intent(requireContext(), FloatingButtonService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("overlay", "android>8")
            requireContext().startForegroundService(serviceIntent)
        } else {

            Log.d("overlay", "android<8")
            requireContext().startService(serviceIntent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "floating_button_channel",
                "Floating Button Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for floating button service"
            }

            val notificationManager = requireContext().getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}


object ScreenCapturePermissionStore {
    var resultCode: Int = -1
    var dataIntent: Intent? = null
}

