package com.example.frontend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.R
import com.example.frontend.model.BookmarkItem

class BookmarkAdapter(private val items: List<BookmarkItem>) :
    RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1: TextView = itemView.findViewById(R.id.bookmark_text1)
        val text2: TextView = itemView.findViewById(R.id.bookmark_text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_item, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val item = items[position]
        holder.text1.text = item.text1
        holder.text2.text = item.text2
    }

    override fun getItemCount(): Int = items.size
}