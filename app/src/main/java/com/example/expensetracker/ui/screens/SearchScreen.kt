package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.expensetracker.ui.components.ExpenseSearch
import com.example.expensetracker.viewmodel.ExpenseViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(
    viewModel: ExpenseViewModel,
    onNavigateUp: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var hasInputFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    DisposableEffect(keyboardController) {
        val onKeyboardDismissed = {
            if (!hasInputFocus) {
                onNavigateUp()
            }
        }
        onDispose {
            // No specific keyboard dismissal listener to remove
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExpenseSearch(
            viewModel = viewModel,
            isSearchVisible = true,
            onSearchVisibilityChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    hasInputFocus = focusState.hasFocus
                }
        )
    }
}