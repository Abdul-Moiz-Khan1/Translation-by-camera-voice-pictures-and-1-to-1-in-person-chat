package com.example.frontend.presentation.view

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.presentation.adapters.HistoryAdapter
import com.example.frontend.databinding.ActivityHistoryBinding
import com.example.frontend.domain.model.HistoryItem
import com.example.frontend.presentation.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class History : AppCompatActivity() {
    private val viewmodel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnHistory.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            val historyList = viewmodel.getHistoryOnce() as MutableList<HistoryItem>
            binding.historyRecView.adapter = HistoryAdapter(historyList)
            binding.historyRecView.layoutManager = LinearLayoutManager(this@History)
        }
    }
}