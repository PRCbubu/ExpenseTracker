package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.database.entities.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExpenseViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _largestSpent = MutableStateFlow<Expense?>(null)
    val largestSpent: StateFlow<Expense?> = _largestSpent

    private val _largestSaved = MutableStateFlow<Expense?>(null)
    val largestSaved: StateFlow<Expense?> = _largestSaved

    init {
        viewModelScope.launch {
            expenseRepository.allExpenses.collectLatest { expenses ->
                _expenses.value = expenses
            }
        }
        viewModelScope.launch {
            expenseRepository.largestAmountSpent.collectLatest { expense ->
                _largestSpent.value = expense
            }
        }
        viewModelScope.launch {
            expenseRepository.largestAmountSaved.collectLatest { expense ->
                _largestSaved.value = expense
            }
        }
    }

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
}