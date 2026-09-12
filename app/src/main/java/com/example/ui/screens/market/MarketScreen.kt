package com.example.ui.screens.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    viewModel: MarketViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Live Market Prices", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter Symbol (e.g., AAPL, BTCUSD)") },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                IconButton(onClick = { viewModel.fetchQuote() }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestionChip(onClick = { viewModel.updateSearchQuery("BTCUSD"); viewModel.fetchQuote() }, label = { Text("BTC") })
            SuggestionChip(onClick = { viewModel.updateSearchQuery("ETHUSD"); viewModel.fetchQuote() }, label = { Text("ETH") })
            SuggestionChip(onClick = { viewModel.updateSearchQuery("AAPL"); viewModel.fetchQuote() }, label = { Text("AAPL") })
            SuggestionChip(onClick = { viewModel.updateSearchQuery("TSLA"); viewModel.fetchQuote() }, label = { Text("TSLA") })
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (val state = uiState) {
            is MarketUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Search for a symbol to see live data.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            is MarketUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is MarketUiState.Success -> {
                val quote = state.quote
                val changeValue = quote.change.toDoubleOrNull() ?: 0.0
                val isPositive = changeValue >= 0
                val trendColor = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000)
                val trendIcon = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = quote.symbol,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${quote.price.formatPrice()}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${quote.change} (${quote.changePercent})",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = trendColor
                            )
                        }
                    }
                }
            }
            is MarketUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun String.formatPrice(): String {
    return this.toDoubleOrNull()?.let { 
        if (it > 1000) String.format("%,.2f", it) else this 
    } ?: this
}
