package com.example.frontend.presentation.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.frontend.R

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [fragChat.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragChat : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.blueSpeaker).setColorFilter(ContextCompat.getColor(requireContext() , R.color.appBlue), PorterDuff.Mode.SRC_IN)
        view.findViewById<ImageView>(R.id.convertImg).setColorFilter(ContextCompat.getColor(requireContext() , R.color.appBlue), PorterDuff.Mode.SRC_IN)


    }
}