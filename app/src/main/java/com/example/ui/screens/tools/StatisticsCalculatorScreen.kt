package com.example.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.StatisticsEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rawInput by remember { mutableStateOf("") }
    
    val result = remember(rawInput) {
        val numbers = rawInput.split(Regex("[,\\s]+"))
            .filter { it.isNotBlank() }
            .mapNotNull { it.toDoubleOrNull() }
        StatisticsEngine.calculateStatistics(numbers)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Statistics Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Enter numbers separated by commas or spaces:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = rawInput,
                onValueChange = { rawInput = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("e.g. 1, 2.5, 3, 4, 5.5") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (result != null) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Count (n)", style = MaterialTheme.typography.bodyMedium)
                            Text(result.count.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sum (Σx)", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%,.4f", result.sum).replace(Regex("\\.?0+$"), ""), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Mean (x̄)", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                            Text(String.format("%,.4f", result.mean).replace(Regex("\\.?0+$"), ""), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Median", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                            Text(String.format("%,.4f", result.median).replace(Regex("\\.?0+$"), ""), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Mode", style = MaterialTheme.typography.bodyMedium)
                            Text(result.mode.joinToString { String.format("%,.4f", it).replace(Regex("\\.?0+$"), "") }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Standard Deviation (σ)", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%,.4f", result.standardDeviation), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Variance (σ²)", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%,.4f", result.variance), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Min / Max", style = MaterialTheme.typography.bodyMedium)
                            Text("${String.format("%,.4f", result.min).replace(Regex("\\.?0+$"), "")} / ${String.format("%,.4f", result.max).replace(Regex("\\.?0+$"), "")}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Range", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%,.4f", result.range).replace(Regex("\\.?0+$"), ""), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (rawInput.isNotBlank()) {
                Text("Invalid input data", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
