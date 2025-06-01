package com.example.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.screens.getExpenseColor

@Composable
fun StatDisplayBox(
    amount: Double,
    isPositive: Boolean = false,
    label: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isPositive) {
        Color(0xFF4CAF50).copy(alpha = 0.7f)
    } else {
        Color(0xFFF44336).copy(alpha = 0.7f)
    }

    var showFullAmount by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (showFullAmount) 1.0f else 0.7f, label = "")

    val formattedAmount = "₹${String.format("%.2f", amount)}"
    val displayAmount by remember(showFullAmount, formattedAmount) {
        mutableStateOf(
            if (showFullAmount) {
                formattedAmount
            } else if (formattedAmount.length <= 12) {
                formattedAmount
            } else {
                formattedAmount.take(10) + "..."
            }
        )
    }

    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable { showFullAmount = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayAmount,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = getExpenseColor(amount),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Overlay to detect outside tap
        if (showFullAmount) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = { showFullAmount = false })
            )
        }
    }
}