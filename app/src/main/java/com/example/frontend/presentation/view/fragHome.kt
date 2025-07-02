package com.example.frontend.presentation.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.frontend.R
import com.example.frontend.presentation.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class fragHome : Fragment() {

    private var translationHandler: Handler? = null
    private var translationRunnable: Runnable? = null
    private var isPowerOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_frag_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toggleLayout = view.findViewById<LinearLayout>(R.id.toggleButton)
        val powerText = view.findViewById<TextView>(R.id.powerText)
        val powerIcon = view.findViewById<ImageView>(R.id.powerIcon)
        val settingsBtn = view.findViewById<ImageView>(R.id.settingsBtn)
        val originalBtn = view.findViewById<TextView>(R.id.originalBtn)
        val translatedBtn = view.findViewById<TextView>(R.id.translatedBtn)


        originalBtn.setOnClickListener {
            originalBtn.setBackgroundResource(R.drawable.bg_left_selected)
            translatedBtn.setBackgroundResource(R.drawable.bg_left_unselected)
            originalBtn.setTextColor(Color.WHITE)
            translatedBtn.setTextColor(Color.BLACK)

            // 👉 Add your logic here to show "Original" text
        }

        translatedBtn.setOnClickListener {
            translatedBtn.setBackgroundResource(R.drawable.bg_right_selected)
            originalBtn.setBackgroundResource(R.drawable.bg_right_unselected)
            translatedBtn.setTextColor(Color.BLACK)
            originalBtn.setTextColor(Color.WHITE)

            // 👉 Add your logic here to show "Translated" text
        }


        settingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

        view.findViewById<EditText>(R.id.originalText_fragHome).addTextChangedListener(object : TextWatcher {
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
            }

            override fun afterTextChanged(p0: Editable?) {

                translationRunnable?.let { translationHandler?.removeCallbacks(it) } // cancel previous calls

                translationHandler = Handler(Looper.getMainLooper())
                translationRunnable = Runnable {
                    val content = p0.toString()
                    val lang = view.findViewById<TextView>(R.id.toLanguageText).text.toString()

                    val intent = Intent(requireContext(), TextTranslation::class.java)
                    intent.putExtra("content", content)
                    intent.putExtra("toLanguage", lang)
                    startActivity(intent)
                }

                translationHandler?.postDelayed(translationRunnable!!, 600)
            }
        })

        toggleLayout.setOnClickListener {
            isPowerOn = !isPowerOn

            if (isPowerOn) {
                toggleLayout.setBackgroundResource(R.drawable.bg_toggle_on)
                powerText.text = "ON"
                powerIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.appBlue))
                toggleLayout.rotation = 180f
                powerIcon.rotation = 180f
                powerText.rotation = 180f
            } else {
                toggleLayout.setBackgroundResource(R.drawable.bg_togglwe)
                powerText.text = "OFF"
                powerIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.appOrange
                    )
                )
                toggleLayout.rotation = 0f
                powerIcon.rotation = 0f
                powerText.rotation = 0f
            }
        }


        view.findViewById<LinearLayout>(R.id.to_lanugage_home).setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        translationRunnable?.let { translationHandler?.removeCallbacks(it) }
    }
}