package com.example.frontend

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getColor


class fragHome : Fragment() {
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
        val languageTextView = view.findViewById<TextView>(R.id.fromlangtext)
        val To_languageTextView = view.findViewById<TextView>(R.id.toLanguageText)
        val settingsBtn = view.findViewById<ImageView>(R.id.settingsBtn)

        settingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

        toggleLayout.setOnClickListener {
            isPowerOn = !isPowerOn

            if (isPowerOn) {
                toggleLayout.setBackgroundResource(R.drawable.bg_toggle_on)
                powerText.text = "ON"
                powerIcon.setColorFilter(getColor(requireContext(), R.color.appBlue))
                toggleLayout.rotation = 180f
                powerIcon.rotation = 180f
                powerText.rotation = 180f
            } else {
                toggleLayout.setBackgroundResource(R.drawable.bg_togglwe)
                powerText.text = "OFF"
                powerIcon.setColorFilter(getColor(requireContext(), R.color.appOrange))
                toggleLayout.rotation = 0f
                powerIcon.rotation = 0f
            }
        }

        val languagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedLang = result.data?.getStringExtra("SELECTED_LANGUAGE")
                    languageTextView.text = selectedLang
                }
            }
        val languagePickerLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedLang = result.data?.getStringExtra("SELECTED_LANGUAGE")
                    To_languageTextView.text = selectedLang
                }
            }
        view.findViewById<LinearLayout>(R.id.from_language).setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            languagePickerLauncher.launch(intent)
        }
        view.findViewById<LinearLayout>(R.id.to_lanugage).setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            languagePickerLauncher2.launch(intent)
        }
    }
}
