package com.example.expensetracker.data.repository

import com.example.expensetracker.data.database.ExpenseDao
import com.example.expensetracker.data.database.entities.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val largestAmountSpent: Flow<Expense?> = expenseDao.getLargestAmountSpent()
    val largestAmountSaved: Flow<Expense?> = expenseDao.getLargestAmountSaved()

    suspend fun insertExpense(expense: Expense){
        withContext(Dispatchers.IO){
            expenseDao.insertExpense(expense)
        }
    }

    suspend fun deleteExpense(expense: Expense){
        withContext(Dispatchers.IO){
            expenseDao.deleteExpense(expense)
        }
    }

}