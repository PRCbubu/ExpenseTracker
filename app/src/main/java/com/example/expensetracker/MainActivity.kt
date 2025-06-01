package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.expensetracker.data.database.ExpenseDatabase
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.screens.ExpenseTrackerApp
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModelFactory
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, ExpenseDatabase::class.java, "expenses_database").build()
        val repository = ExpenseRepository(db.expenseDao())
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val viewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(repository))
                ExpenseTrackerApp(viewModel)
            }
        }
    }
}