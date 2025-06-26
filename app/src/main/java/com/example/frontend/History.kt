package com.example.frontend

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.adapters.HistoryAdapter
import com.example.frontend.databinding.ActivityHistoryBinding
import com.example.frontend.model.HistoryItem

class History : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnHistory.setOnClickListener {
            finish()
        }


        val historyList = listOf(
            HistoryItem("English", "Hello", "Hallo"),
            HistoryItem("French", "Bonjour", "Bonjour"),
            HistoryItem("Spanish", "Hola", "Hola"),
            HistoryItem("German", "Hallo", "Hallo"),
            HistoryItem("Italian", "Ciao", "Ciao"),
            HistoryItem("Japanese", "moiad", "asd2"),
            HistoryItem("Chinese", "你好", "你好"),
            HistoryItem("Korean", "안녕하세요", "안녕하세요"),
            HistoryItem("Russian", "Здравствуйте", "Здравствуйте"),

            HistoryItem("French", "Bonjour", "Bonjour"),
            HistoryItem("Spanish", "Hola", "Hola"),
            HistoryItem("German", "Hallo", "Hallo"),
            HistoryItem("Italian", "Ciao", "Ciao"),

            HistoryItem("Italian", "Ciao", "Ciao"),
            HistoryItem("Japanese", "moiad", "asd2"),
            HistoryItem("Chinese", "你好", "你好"),
            HistoryItem("Korean", "안녕하세요", "안녕하세요"),
            HistoryItem("Russian", "Здравствуйте", "Здравствуйте"),


            )

        binding.historyRecView.adapter = HistoryAdapter(historyList)
        binding.historyRecView.layoutManager = LinearLayoutManager(this)


    }
}