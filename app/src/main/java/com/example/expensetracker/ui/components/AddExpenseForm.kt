package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseForm(
    amountText: String,
    descriptionText: String,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddExpense: (Double, String) -> Unit,
    onFormVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier // Keep the modifier parameter
) {
    val focusRequester = remember { FocusRequester() }
    var showDescriptionField by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        modifier = modifier // Apply the passed modifier first (e.g., for alignment from parent)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        // The Column inside the card remains the same
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding inside the card is fine
            verticalArrangement = Arrangement.SpaceBetween // Changed to SpaceBetween might give better spacing
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add New Expense",
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { onFormVisibilityChange(false) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    // Basic validation for decimal numbers (up to 2 decimal places)
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        // Allow deleting the dot or trailing zeros
                        if(it.endsWith(".") || it.matches(Regex("^\\d+\\.?\\d*\$"))) {
                            onAmountChange(it)
                        } else if (it.matches(Regex("^\\d+\$")) || it.isEmpty()){ // Allows integers
                            onAmountChange(it)
                        } else {
                            // Format to max 2 decimal places if valid number entered
                            val num = it.toDoubleOrNull()
                            if(num != null) onAmountChange(String.format("%.2f", num)) else onAmountChange(it)
                        }
                    } else if (it.matches(Regex("^\\d+\\.\$"))){ // Allows typing the dot
                        onAmountChange(it)
                    }
                },
                label = { Text("Amount (₹)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // Use Decimal
                singleLine = true
            )


            Spacer(modifier = Modifier.height(8.dp))

            if (showDescriptionField) {
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true // Consider multi-line if needed
                )
            } else {
                OutlinedButton(onClick = { showDescriptionField = true }) {
                    Text("Add Details (Optional)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ExpenseButton(
                    amountText = amountText,
                    descriptionText = descriptionText,
                    onAddExpense = onAddExpense,
                    onFormVisibilityChange = onFormVisibilityChange,
                    amountMultiplier = -1.0, // Spent
                    buttonText = "Spent",
                    buttonColor = Color.Red
                )

                ExpenseButton(
                    amountText = amountText,
                    descriptionText = descriptionText,
                    onAddExpense = onAddExpense,
                    onFormVisibilityChange = onFormVisibilityChange,
                    amountMultiplier = 1.0, // Earned
                    buttonText = "Earned",
                    buttonColor = Color(0xFF4CAF50) // Use Color resource ideally
                )
            }
            // Add a little extra space at the bottom inside the card if needed,
            // though imePadding should handle the main keyboard spacing.
            // Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ExpenseButton(
    amountText: String,
    descriptionText: String,
    onAddExpense: (Double, String) -> Unit,
    onFormVisibilityChange: (Boolean) -> Unit,
    amountMultiplier: Double,
    buttonText: String,
    buttonColor: Color,
) {
    OutlinedButton(
        onClick = {
            val amount = amountText.toDoubleOrNull()
            if (amount != null) {
                onAddExpense(amount * amountMultiplier, descriptionText)
                onFormVisibilityChange(false)
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(text = buttonText)
    }
}