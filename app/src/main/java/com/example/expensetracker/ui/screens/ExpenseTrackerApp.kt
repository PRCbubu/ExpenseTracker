package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import com.example.expensetracker.viewmodel.ExpenseViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.components.ExpenseTrackerTopBar
import kotlinx.coroutines.launch
import kotlin.math.exp

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val green = Color(0xFF4CAF50)
val red = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp(viewModel: ExpenseViewModel) {
    var isFormVisible by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()
    var showFab by rememberSaveable { mutableStateOf(true) }
    var isSyncScrollEnabled by rememberSaveable { mutableStateOf(false) } // Lifted state

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(route = "home", label = "Home", icon = Icons.Filled.Home),
            BottomNavItem(route = "expenses", label = "Expenses", icon = Icons.AutoMirrored.Filled.List),
            BottomNavItem(route = "about", label = "About", icon = Icons.Filled.Info)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0) { bottomNavItems.size }
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val currentRoute by remember { derivedStateOf { bottomNavItems.getOrNull(currentPage)?.route } }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentPage) {
        showFab = currentRoute == "home"
    }

    Scaffold(
        topBar = {
            val screenName = when (currentRoute) {
                "home" -> "Expense Tracker"
                "expenses" -> "Expense List"
                "about" -> "About App"
                "search" -> "Search Expenses" // Title for the search screen
                else -> "Expense Tracker"
            }
            ExpenseTrackerTopBar(
                screenName = screenName,
                actions = {
                    if (currentRoute == "expenses") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sync Scroll")
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isSyncScrollEnabled,
                                onCheckedChange = { isSyncScrollEnabled = it },
                                thumbContent = {
                                    Icon(
                                        imageVector = if (isSyncScrollEnabled) Icons.Filled.Sync else Icons.Filled.SyncDisabled,
                                        contentDescription = if (isSyncScrollEnabled) "Sync On" else "Sync Off",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            )
        },
        floatingActionButton = { if (showFab && !isFormVisible && currentRoute == "home") AddExpenseFab(onClick = { isFormVisible = true }) },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues -> // Use the paddingValues here
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply the paddingValues to the HorizontalPager
            userScrollEnabled = true
        ) { page ->
            when (bottomNavItems[page].route) {
                "home" -> ExpenseTrackerHomeScreen(viewModel, navController, isFormVisible) { isFormVisible = it }
                "expenses" -> ExpenseListScreen(viewModel, isSyncScrollEnabled)
                "about" -> AboutAppScreen(navController)
                // "search" composable is no longer part of the HorizontalPager
            }
        }
    }
}

@Composable
fun AddExpenseFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = "Add Expense")
    }
}

fun getExpenseColor(total: Double): Color {
    val clampedTotal = total.toFloat().coerceIn(0f, 1f)
    val ratio = 1 / (1 + exp(-clampedTotal / 1000))
    return lerp(red, green, ratio.toFloat())
}