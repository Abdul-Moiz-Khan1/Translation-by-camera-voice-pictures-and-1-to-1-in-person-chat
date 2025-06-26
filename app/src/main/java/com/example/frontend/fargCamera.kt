package com.example.frontend

import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.frontend.databinding.FragmentFargCameraBinding
import java.util.jar.Manifest

class fargCamera : Fragment() {

    private var _binding: FragmentFargCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFargCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = binding.previewView

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        binding.capture.setOnClickListener {
            binding.translateBtnLayout.visibility = View.VISIBLE
            binding.bottomLayout.visibility = View.INVISIBLE
            binding.topButtons.visibility = View.INVISIBLE
            binding.backAndCrop.visibility = View.VISIBLE
        }

        binding.translateFin.setOnClickListener {
            binding.translateBtnLayout.visibility = View.INVISIBLE
            binding.backAndCrop.visibility = View.INVISIBLE
            binding.finalTopLayout.visibility = View.VISIBLE
            binding.FinalbottomLayout.visibility = View.VISIBLE
        }

        binding.blackdownload.setOnClickListener {
            startActivity(Intent(requireContext(), PictureTranslation::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }
}
