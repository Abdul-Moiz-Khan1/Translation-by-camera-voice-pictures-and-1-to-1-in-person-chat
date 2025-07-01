package com.example.frontend.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.R
import com.example.frontend.domain.model.HistoryItem

class HistoryAdapter(private val items: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lang: TextView = itemView.findViewById(R.id.history_language)
        val orig: TextView = itemView.findViewById(R.id.history_originalText)
        val trans: TextView = itemView.findViewById(R.id.history_translatedText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.lang.text = item.language
        holder.orig.text = item.originalText
        holder.trans.text = item.translatedText
    }

    override fun getItemCount(): Int = items.size
}