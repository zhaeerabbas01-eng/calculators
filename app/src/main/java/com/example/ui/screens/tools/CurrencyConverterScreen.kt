package com.example.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    onBack: () -> Unit,
    viewModel: CurrencyViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val amount by viewModel.amount.collectAsState()
    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val toCurrency by viewModel.toCurrency.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRate()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Currency Converter") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Amount", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { viewModel.updateAmount(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CurrencyDropdownMenu(
                        label = "From",
                        selectedCurrency = fromCurrency,
                        onCurrencySelected = { viewModel.setFromCurrency(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(onClick = { viewModel.swapCurrencies() }) {
                Icon(Icons.Default.SwapVert, contentDescription = "Swap Currencies", modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CurrencyDropdownMenu(
                        label = "To",
                        selectedCurrency = toCurrency,
                        onCurrencySelected = { viewModel.setToCurrency(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    when (val state = uiState) {
                        is CurrencyUiState.Idle, is CurrencyUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is CurrencyUiState.Success -> {
                            val amountVal = amount.toDoubleOrNull() ?: 0.0
                            val result = amountVal * state.rate
                            
                            Text("Result", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${String.format("%,.2f", result)} ${toCurrency.code}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "1 ${fromCurrency.code} = ${String.format("%.4f", state.rate)} ${toCurrency.code}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                            val dateStr = sdf.format(Date(state.timestamp))
                            Text(
                                text = "Last updated: $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        is CurrencyUiState.Error -> {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                Text(state.message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.fetchRate(forceRefresh = true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Rate")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdownMenu(
    label: String,
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "${selectedCurrency.flag} ${selectedCurrency.name} (${selectedCurrency.code})",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = MaterialTheme.shapes.large
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                SUPPORTED_CURRENCIES.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text("${currency.flag} ${currency.name} (${currency.code})") },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
