package com.example.expensetracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.expensetracker.data.database.entities.Expense
import com.example.expensetracker.ui.components.AddExpenseForm
import com.example.expensetracker.ui.components.StatDisplayBox
import com.example.expensetracker.ui.components.TotalExpensesCard
import com.example.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun ExpenseTrackerHomeScreen(
    viewModel: ExpenseViewModel,
    navController: NavHostController, // Keep if navigation needed
    isFormVisible: Boolean,
    onFormVisibilityChange: (Boolean) -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    val largestAmountSpent by viewModel.largestSpent.collectAsState()
    val largestAmountSaved by viewModel.largestSaved.collectAsState()
    val totalExpenses = expenses.sumOf { it.amount ?: 0.0 }

    var amountText by rememberSaveable { mutableStateOf("") }
    var descriptionText by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TotalExpensesCard(totalExpenses = totalExpenses)
            Spacer(modifier = Modifier.height(16.dp))

            if (expenses.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    largestAmountSaved?.let {
                        Box(
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            StatDisplayBox(amount = it.amount ?: 0.0, isPositive = true, label = "Highest Saved")
                        }
                    } ?: Box(Modifier.weight(1f))

                    largestAmountSpent?.let {
                        Box(
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            StatDisplayBox(amount = it.amount ?: 0.0, isPositive = false, label = "Highest Spent")
                        }
                    } ?: Box(Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp),
                    text = "No expenses yet."
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            PlaceholderSearchBar(
                onClick = { navController.navigate("search") } // Navigate to the search screen
            )
        }

        if (isFormVisible) {
            AddExpenseForm(
                amountText = amountText,
                descriptionText = descriptionText,
                onAmountChange = { amountText = it },
                onDescriptionChange = { descriptionText = it },
                onAddExpense = { amount, description ->
                    viewModel.insertExpense(Expense(amount = amount, description = description))
                    amountText = ""
                    descriptionText = ""
                    onFormVisibilityChange(false)
                },
                onFormVisibilityChange = onFormVisibilityChange,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .zIndex(1f)
            )
        }
    }
}

@Composable
fun PlaceholderSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(50))
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Expenses Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search expenses...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}