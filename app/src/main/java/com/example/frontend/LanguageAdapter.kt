package com.example.frontend

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(
    private val languages: List<String>,
    private var selectedPosition: Int = -1,
    private val onItemClick: (String, Int) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val langText: TextView = itemView.findViewById(R.id.languageText)
        val radioBtn: ImageView = itemView.findViewById(R.id.radioButton)
        val cardView: CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.language_item, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.langText.text = language

        val isSelected = position == selectedPosition
        holder.cardView.setBackgroundResource(
            if (isSelected) R.drawable.item_lang_selected else R.drawable.item_lang_unselected
        )


        holder.radioBtn.setImageResource(
            if (isSelected) R.drawable.checked
            else R.drawable.unchecked
        )

        holder.itemView.setOnClickListener {
            updateSelection(holder.adapterPosition)
            onItemClick(language, position)
        }

        holder.radioBtn.setOnClickListener {
            updateSelection(holder.adapterPosition)
            onItemClick(language, position)
        }

    }

    private fun updateSelection(newPosition: Int) {
        if (selectedPosition != newPosition) {
            val prev = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(prev)
            notifyItemChanged(newPosition)
        }
    }

    override fun getItemCount(): Int = languages.size
}
