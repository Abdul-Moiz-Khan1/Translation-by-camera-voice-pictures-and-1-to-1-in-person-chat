package com.example.frontend.presentation.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        settingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

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
}