// File: com/example/expensetracker/ui/components/ExpenseSearch.kt
package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.database.entities.Expense
import com.example.expensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.delay // Import for delay
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseSearch(
    viewModel: ExpenseViewModel,
    isSearchVisible: Boolean,
    onSearchVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val expenses by viewModel.expenses.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val filteredResults = remember(searchQuery, expenses) {
        if (searchQuery.isBlank()) { // Good practice to handle blank query explicitly
            emptyList()
        } else {
            expenses.filter { it.description?.contains(searchQuery, ignoreCase = true) == true }
                .sortedWith(
                    compareBy<Expense> { it.description } // Sort by description first
                        .thenByDescending { it.date } // Then by newest date first
                )
        }
    }

    LaunchedEffect(isSearchVisible) {
        if (isSearchVisible) {
            delay(100) // e.g., 100 milliseconds
            focusRequester.requestFocus()
        } else {
            searchQuery = ""
            // Optional: Clear focus explicitly
            // keyboardController?.hide() // Already done in item click, maybe needed here too?
        }
    }

    AnimatedVisibility(
        visible = isSearchVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Search Expenses") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (searchQuery.isNotEmpty()) {
                if (filteredResults.isNotEmpty()) {
                    LazyColumn {
                        items(filteredResults) { expense ->
                            SearchResultItem(
                                expense = expense,
                                onClick = {
                                    keyboardController?.hide()
                                    onSearchVisibilityChange(false)
                                }
                            )
                        }
                    }
                } else {
                    Text("No matching results", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(expense: Expense, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.weight(1f), text = expense.description ?: "No description")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(expense.date),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
