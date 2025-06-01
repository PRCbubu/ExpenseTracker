package com.example.expensetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expensetracker.data.database.entities.Expense

@Database(entities = [Expense::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)

abstract class ExpenseDatabase: RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}