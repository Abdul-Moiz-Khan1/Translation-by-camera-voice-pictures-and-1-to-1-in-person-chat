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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
    lateinit var translatedBitmap: Bitmap
    var originalOcr: String? = "abc"
    var translatedOcr: String? = "abc"
    val adRequest = AdRequest.Builder().build()
    private var mInterstitialAd: InterstitialAd? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
           finalUri = uri
            setFrame(finalUri)

            Log.d("SelectedImage", "URI: $uri")
        } else {
            Log.d("SelectedImage", "No media selected")
        }
    }

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

        InterstitialAd.load(requireContext(),
            "ca-app-pub-3940256099942544/1033173712", // Test ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Log.d("AdDemo", "Interstitial ad loaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d("AdDemo", "Failed to load interstitial: ${loadAdError.message}")
                    mInterstitialAd = null
                }
            })
        languagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedLanguage = result.data?.getStringExtra("selected_language_for_camera")
                selectedLanguage?.let {
                    Log.d("HomeFragment", "Selected: $it")
                    binding.toLanguageCameraView.text = it
                }
            }
        }

        val originalBtn = view.findViewById<TextView>(R.id.originalBtn)
        val translatedBtn = view.findViewById<TextView>(R.id.translatedBtn)


        originalBtn.setOnClickListener {
            originalBtn.setBackgroundResource(R.drawable.bg_right_selected)
            translatedBtn.setBackgroundResource(R.drawable.bg_left_unselected)
            originalBtn.setTextColor(Color.BLACK)
            translatedBtn.setTextColor(Color.WHITE)
            binding.capturedFrame.setImageURI(finalUri)

        }

        translatedBtn.setOnClickListener {
            if(!::translatedBitmap.isInitialized){
                Toast.makeText(requireContext(), "Processing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            translatedBtn.setBackgroundResource(R.drawable.bg_right_selected)
            originalBtn.setBackgroundResource(R.drawable.bg_right_unselected)
            translatedBtn.setTextColor(Color.BLACK)
            originalBtn.setTextColor(Color.WHITE)
            binding.capturedFrame.setImageBitmap(translatedBitmap)

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
        binding.fromGallery.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

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
                        setFrame(savedUri)

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

        binding.speakFragCamera.setOnClickListener {
            viewModel.speak(translatedOcr.toString())
        }
        binding.downloadFragCamera.setOnClickListener {
            viewModel.saveBitmapToGallery(requireContext(), translatedBitmap)
        }

        binding.translateFin.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        processImageAndViews()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        mInterstitialAd = null
                        processImageAndViews()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("AdDemo", "Ad shown.")
                    }
                }

                mInterstitialAd?.show(requireActivity())
            } else {
                Log.d("AdDemo", "Ad not ready. Loading now...")
                // Load ad and then do fallback work
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(requireContext(),
                    "ca-app-pub-3940256099942544/1033173712",
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            mInterstitialAd = ad
                            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    mInterstitialAd = null
                                    processImageAndViews()
                                }
                            }
                            ad.show(requireActivity())
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.d("AdDemo", "Ad failed to load: ${loadAdError.message}")
                            processImageAndViews()
                        }
                    })
            }
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
    private fun fargCamera.processImageAndViews() {
        viewModel.processImageAndTranslate(
            requireContext(),
            finalUri,
            binding.toLanguageCameraView.text.toString()
        ) { finalBitmap, original, translated ->
            translatedBitmap = finalBitmap
            originalOcr = original
            translatedOcr = translated
            binding.capturedFrame.setImageBitmap(finalBitmap)
        }

        binding.translateBtnLayout.visibility = View.INVISIBLE
        binding.backAndCrop.visibility = View.INVISIBLE
        binding.finalTopLayout.visibility = View.VISIBLE
        binding.FinalbottomLayout.visibility = View.VISIBLE
    }
    private fun setFrame(savedUri: Uri) {
        binding.capturedFrame.apply {
            visibility = View.VISIBLE
            setImageURI(savedUri)
            finalUri = savedUri
            binding.previewView.visibility = View.INVISIBLE
            binding.translateBtnLayout.visibility = View.VISIBLE
            binding.bottomLayout.visibility = View.INVISIBLE
            binding.topButtons.visibility = View.INVISIBLE
            binding.backAndCrop.visibility = View.VISIBLE
        }
    }
}


