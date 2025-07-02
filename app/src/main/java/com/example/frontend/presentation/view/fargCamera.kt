package com.example.frontend.presentation.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.frontend.R
import com.example.frontend.databinding.FragmentFargCameraBinding
import com.example.frontend.presentation.viewModel.HomeViewModel
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class fargCamera : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var languagePickerLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentFargCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null
    lateinit var finalUri: Uri
    var originalOcr: String? = "abc"
    var translatedOcr: String? = "abc"

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            if (croppedUri != null) {
                finalUri = croppedUri
            }
            binding.capturedFrame.setImageURI(croppedUri)
        } else {
            val error = result.error
            Toast.makeText(requireContext(), "Crop failed: ${error?.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

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

        languagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedLanguage = result.data?.getStringExtra("selected_language_for_camera")
                selectedLanguage?.let {
                    // ✅ Use the selected language here
                    Log.d("HomeFragment", "Selected: $it")
                    binding.toLanguageCameraView.text = it // for example
                }
            }
        }

        binding.toLanguageCameraView.setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            intent.putExtra("from", "camera")
            languagePickerLauncher.launch(intent)
        }

        binding.cropButton.setOnClickListener {
            val currentImageUri = (binding.capturedFrame.drawable as? BitmapDrawable)?.bitmap?.let {
                val file = File(requireContext().cacheDir, "temp_crop.jpg")
                file.outputStream().use { out -> it.compress(Bitmap.CompressFormat.JPEG, 100, out) }
                Uri.fromFile(file)
            }

            currentImageUri?.let { uri ->
                val cropOptions = CropImageContractOptions(
                    uri,
                    CropImageOptions().apply {
                        cropShape = CropImageView.CropShape.RECTANGLE
                        guidelines = CropImageView.Guidelines.ON
                        activityMenuIconColor = Color.WHITE
                        activityTitle = "Crop Image"
                        outputCompressFormat = Bitmap.CompressFormat.JPEG
                        outputCompressQuality = 90
                        showCropOverlay = true
                    }
                )

                cropImageLauncher.launch(cropOptions)

            } ?: Toast.makeText(requireContext(), "No image to crop", Toast.LENGTH_SHORT).show()
        }

        previewView = binding.previewView

        binding.backncropbackButton.setOnClickListener {
            binding.previewView.visibility = View.VISIBLE
            binding.capturedFrame.visibility = View.GONE
            binding.translateBtnLayout.visibility = View.GONE
            binding.bottomLayout.visibility = View.VISIBLE
            binding.topButtons.visibility = View.VISIBLE
            binding.backAndCrop.visibility = View.GONE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.capture.setOnClickListener {

            val imageCapture = imageCapture ?: return@setOnClickListener

            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                File(
                    requireContext().cacheDir,
                    "captured_${System.currentTimeMillis()}.jpg"
                )
            ).build()

            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = outputFileResults.savedUri ?: Uri.fromFile(
                            File(
                                requireContext().cacheDir,
                                "captured_${System.currentTimeMillis()}.jpg"
                            )
                        )
                        binding.previewView.visibility = View.GONE
                        binding.capturedFrame.apply {
                            visibility = View.VISIBLE
                            setImageURI(savedUri)
                            finalUri = savedUri
                            binding.translateBtnLayout.visibility = View.VISIBLE
                            binding.bottomLayout.visibility = View.INVISIBLE
                            binding.topButtons.visibility = View.INVISIBLE
                            binding.backAndCrop.visibility = View.VISIBLE
                        }
                    }


                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            requireContext(),
                            "Capture failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("CameraCapture", "Capture error", exception)
                    }
                }
            )

        }

        binding.translateFin.setOnClickListener {
            viewModel.processImageAndTranslate(
                requireContext(),
                finalUri,
                binding.toLanguageCameraView.text.toString()
            ) { finalBitmap, original, translated ->
                originalOcr = original
                translatedOcr = translated
                binding.capturedFrame.setImageBitmap(finalBitmap)
            }

            binding.translateBtnLayout.visibility = View.INVISIBLE
            binding.backAndCrop.visibility = View.INVISIBLE
            binding.finalTopLayout.visibility = View.VISIBLE
            binding.FinalbottomLayout.visibility = View.VISIBLE
        }

        binding.copyFragCamera.setOnClickListener {
            val intent = Intent(requireContext(), PictureTranslation::class.java)
            intent.putExtra("original", originalOcr)
            intent.putExtra("translated", translatedOcr)
            startActivity(intent)
        }

        binding.retakeFragCamera.setOnClickListener {
            binding.previewView.visibility = View.VISIBLE
            binding.capturedFrame.visibility = View.GONE
            binding.translateBtnLayout.visibility = View.GONE
            binding.bottomLayout.visibility = View.VISIBLE
            binding.topButtons.visibility = View.VISIBLE
            binding.finalTopLayout.visibility = View.INVISIBLE
            binding.FinalbottomLayout.visibility = View.INVISIBLE
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
