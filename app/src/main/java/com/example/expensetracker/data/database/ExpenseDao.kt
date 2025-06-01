package com.example.expensetracker.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.database.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE amount < 0 ORDER BY amount ASC LIMIT 1")
    fun getLargestAmountSpent(): Flow<Expense?>

    @Query("SELECT * FROM expenses WHERE amount > 0 ORDER BY amount DESC LIMIT 1")
    fun getLargestAmountSaved(): Flow<Expense?>
}