package com.example.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.FinanceEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("EMI", "Compound", "Simple")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finance Calculators") },
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
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> EmiCalculatorView()
                    1 -> CompoundInterestView()
                    2 -> SimpleInterestView()
                }
            }
        }
    }
}

@Composable
fun EmiCalculatorView() {
    var principal by remember { mutableStateOf("100000") }
    var rate by remember { mutableStateOf("10.5") }
    var months by remember { mutableStateOf("24") }

    val result = remember(principal, rate, months) {
        val p = principal.toDoubleOrNull() ?: 0.0
        val r = rate.toDoubleOrNull() ?: 0.0
        val m = months.toIntOrNull() ?: 0
        FinanceEngine.calculateEmi(p, r, m)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = principal,
            onValueChange = { principal = it },
            label = { Text("Loan Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = rate,
            onValueChange = { rate = it },
            label = { Text("Annual Interest Rate (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = months,
            onValueChange = { months = it },
            label = { Text("Tenure (Months)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Monthly EMI", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = String.format("%,.2f", result.monthlyEmi),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Principal", style = MaterialTheme.typography.bodyMedium)
                    Text(String.format("%,.2f", result.totalPrincipal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Interest", style = MaterialTheme.typography.bodyMedium)
                    Text(String.format("%,.2f", result.totalInterest), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Payment", style = MaterialTheme.typography.titleMedium)
                    Text(String.format("%,.2f", result.totalPayment), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CompoundInterestView() {
    var principal by remember { mutableStateOf("10000") }
    var rate by remember { mutableStateOf("5.0") }
    var years by remember { mutableStateOf("10") }
    var compoundFreq by remember { mutableStateOf("12") }

    val result = remember(principal, rate, years, compoundFreq) {
        val p = principal.toDoubleOrNull() ?: 0.0
        val r = rate.toDoubleOrNull() ?: 0.0
        val y = years.toDoubleOrNull() ?: 0.0
        val c = compoundFreq.toIntOrNull() ?: 1
        FinanceEngine.calculateCompoundInterest(p, r, y, c)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = principal,
            onValueChange = { principal = it },
            label = { Text("Initial Investment") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = rate,
                onValueChange = { rate = it },
                label = { Text("Rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = years,
                onValueChange = { years = it },
                label = { Text("Years") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = compoundFreq,
            onValueChange = { compoundFreq = it },
            label = { Text("Compounds per Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Final Balance", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = String.format("%,.2f", result.finalBalance),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Interest Earned", style = MaterialTheme.typography.bodyMedium)
                    Text(String.format("%,.2f", result.totalInterest), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SimpleInterestView() {
    var principal by remember { mutableStateOf("5000") }
    var rate by remember { mutableStateOf("7.5") }
    var years by remember { mutableStateOf("5") }

    val result = remember(principal, rate, years) {
        val p = principal.toDoubleOrNull() ?: 0.0
        val r = rate.toDoubleOrNull() ?: 0.0
        val y = years.toDoubleOrNull() ?: 0.0
        FinanceEngine.calculateSimpleInterest(p, r, y)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = principal,
            onValueChange = { principal = it },
            label = { Text("Principal Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = rate,
            onValueChange = { rate = it },
            label = { Text("Annual Interest Rate (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = years,
            onValueChange = { years = it },
            label = { Text("Time Period (Years)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Amount", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = String.format("%,.2f", result.totalAmount),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Interest Earned", style = MaterialTheme.typography.bodyMedium)
                    Text(String.format("%,.2f", result.interest), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
