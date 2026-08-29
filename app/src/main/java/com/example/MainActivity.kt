package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.CalculatorDatabase
import com.example.data.repository.HistoryRepository
import com.example.ui.CalculatorApp
import com.example.ui.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CalculatorDatabase.getInstance(applicationContext)
        val historyRepository = HistoryRepository(database.historyDao())

        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CalculatorViewModel(historyRepository) as T
            }
        }

        setContent {
            val calcViewModel: CalculatorViewModel = viewModel(factory = viewModelFactory)
            CalculatorApp(viewModel = calcViewModel)
        }
    }
}
