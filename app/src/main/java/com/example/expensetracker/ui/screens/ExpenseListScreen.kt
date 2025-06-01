package com.example.expensetracker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.database.entities.Expense
import com.example.expensetracker.ui.components.ExpenseItemCard
import com.example.expensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel, isSyncScrollEnabled: Boolean) {
    val expensesState = viewModel.expenses.collectAsState()
    val expenses = expensesState.value

    val spentExpenses by remember(expenses) { derivedStateOf { expenses.filter { it.amount?.toDouble() ?: 0.0 < 0 } } }
    val savedExpenses by remember(expenses) { derivedStateOf { expenses.filter { it.amount?.toDouble() ?: 0.0 >= 0 } } }

    val spentListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val savedListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // State to track if the screen is visible (approximation)
    var manualScrollingList by remember { mutableStateOf<LazyListState?>(null) }

    LaunchedEffect(isSyncScrollEnabled, spentListState, savedListState) { // Add list states as keys
        if (isSyncScrollEnabled) {
            // Coroutine to sync from Spent -> Saved
            launch {
                snapshotFlow { spentListState.firstVisibleItemIndex to spentListState.firstVisibleItemScrollOffset }
                    .collect { (index, offset) ->
                        // Only sync if Saved isn't the one currently being scrolled programmatically
                        if (manualScrollingList != savedListState) {
                            if (savedListState.firstVisibleItemIndex != index || savedListState.firstVisibleItemScrollOffset != offset) {
                                manualScrollingList = spentListState // Mark Spent as initiator
                                try {
                                    savedListState.scrollToItem(index, offset)
                                } finally {
                                    if (manualScrollingList == spentListState) {
                                        manualScrollingList = null // Reset after scroll attempt
                                    }
                                }
                            } else {
                                // If positions match, ensure initiator is cleared if it was this list
                                if (manualScrollingList == spentListState) manualScrollingList = null
                            }
                        }
                    }
            }

            // Coroutine to sync from Saved -> Spent
            launch {
                snapshotFlow { savedListState.firstVisibleItemIndex to savedListState.firstVisibleItemScrollOffset }
                    .collect { (index, offset) ->
                        // Only sync if Spent isn't the one currently being scrolled programmatically
                        if (manualScrollingList != spentListState) {
                            if (spentListState.firstVisibleItemIndex != index || spentListState.firstVisibleItemScrollOffset != offset) {
                                manualScrollingList = savedListState // Mark Saved as initiator
                                try {
                                    spentListState.scrollToItem(index, offset)
                                } finally {
                                    if (manualScrollingList == savedListState) {
                                        manualScrollingList = null // Reset after scroll attempt
                                    }
                                }
                            } else {
                                // If positions match, ensure initiator is cleared if it was this list
                                if (manualScrollingList == savedListState) manualScrollingList = null
                            }
                        }
                    }
            }
        }
        // Note: When isSyncScrollEnabled becomes false, LaunchedEffect cancels these jobs.
    }
    // --- End Sync Scrolling Logic ---

    // Simplified visibility check - screen is composed means it's "visible" enough for setup
    // LaunchedEffect(Unit) { isScreenVisible = true } // You might not need isScreenVisible key anymore



    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,

    ) {
        // Spent Expenses Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Spends",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (spentExpenses.isNotEmpty()) {
                LazyColumn(
                    state = spentListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(spentExpenses) { expense ->
                        ExpenseItemCard(expense = expense, onDelete = { viewModel.deleteExpense(expense) })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No spends yet.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Visual Separator
        androidx.compose.material3.VerticalDivider(modifier = Modifier.fillMaxHeight().widthIn(5.dp))

        // Saved Expenses Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Saves",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (savedExpenses.isNotEmpty()) {
                LazyColumn(
                    state = savedListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedExpenses) { expense ->
                        ExpenseItemCard(expense = expense, onDelete = { viewModel.deleteExpense(expense) })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saves yet.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}