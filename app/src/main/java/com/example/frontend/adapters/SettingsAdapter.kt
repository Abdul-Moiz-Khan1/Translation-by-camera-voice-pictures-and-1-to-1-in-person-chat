package com.example.frontend.adapters

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.R
import com.example.frontend.model.ItemData

class SettingsAdapter(
    private val items: List<ItemData>,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val simple = 0
        private const val radio = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].radio) radio else simple
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == radio) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.settings_item_radio_btn, parent, false)
            RadioViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.settings_item_simple, parent, false)
            SimpleViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = items[position]
        when (holder) {
            is RadioViewHolder -> holder.bind(item)
            is SimpleViewHolder -> holder.bind(item)
        }
    }


    override fun getItemCount(): Int = items.size
}


class RadioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: ItemData) {
        val radioImg = itemView.findViewById<ImageView>(R.id.radio_titleImg)
        val radioTitle = itemView.findViewById<TextView>(R.id.radio_title)
        val radioDesc = itemView.findViewById<TextView>(R.id.radio_desc)
        val switchBtn = itemView.findViewById<LinearLayout>(R.id.customSwitch)
        val switchbg = itemView.findViewById<ImageView>(R.id.switchbg)

        var isSwitchOn = false

        switchBtn.setOnClickListener {
            if (isSwitchOn == false) {
                switchBtn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                switchbg.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.appBlue),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                switchBtn.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                switchbg.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.btngry),
                    PorterDuff.Mode.SRC_IN
                )
            }
            isSwitchOn = !isSwitchOn
        }

        radioImg.setImageResource(item.titleImg)
        radioTitle.text = item.title
        radioDesc.text = item.description


    }
}

class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: ItemData) {
        val simpleImg = itemView.findViewById<ImageView>(R.id.simple_titleImg)
        val simpleTitle = itemView.findViewById<TextView>(R.id.simple_title)
        val simpleDesc = itemView.findViewById<TextView>(R.id.simple_desc)
        val gotoBtn = itemView.findViewById<ImageView>(R.id.goto_btn)

        simpleImg.setImageResource(item.titleImg)
        simpleTitle.text = item.title
        simpleDesc.text = item.description

        gotoBtn.setOnClickListener {
            if (item.targetActivity == null) return@setOnClickListener
            val context = itemView.context
            val intent = Intent(context, item.targetActivity)
            context.startActivity(intent)
        }
    }
}
