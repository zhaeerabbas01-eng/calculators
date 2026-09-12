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
import com.example.domain.engine.DateTimeEngine
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Date & Time Calculator") },
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
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Difference") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Add / Subtract") })
            }
            
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (selectedTab == 0) {
                    DateDifferenceView()
                } else {
                    DateAddSubtractView()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDifferenceView() {
    var startMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    // default end is 30 days from now
    var endMillis by remember { mutableStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) }
    
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    val result = remember(startMillis, endMillis) {
        DateTimeEngine.calculateDifference(startMillis, endMillis)
    }
    
    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startMillis)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    state.selectedDateMillis?.let { startMillis = it }
                    showStartPicker = false 
                }) { Text("OK") }
            }
        ) { DatePicker(state = state) }
    }
    
    if (showEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = endMillis)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    state.selectedDateMillis?.let { endMillis = it }
                    showEndPicker = false 
                }) { Text("OK") }
            }
        ) { DatePicker(state = state) }
    }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(modifier = Modifier.fillMaxWidth(), onClick = { showStartPicker = true }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Start Date", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(DateTimeEngine.formatDate(startMillis), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), onClick = { showEndPicker = true }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("End Date", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(DateTimeEngine.formatDate(endMillis), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Difference", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(result, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAddSubtractView() {
    var baseMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showPicker by remember { mutableStateOf(false) }
    
    var yearsStr by remember { mutableStateOf("0") }
    var monthsStr by remember { mutableStateOf("0") }
    var daysStr by remember { mutableStateOf("30") }
    
    var isAddition by remember { mutableStateOf(true) }
    
    val resultMillis = remember(baseMillis, yearsStr, monthsStr, daysStr, isAddition) {
        val y = yearsStr.toIntOrNull() ?: 0
        val m = monthsStr.toIntOrNull() ?: 0
        val d = daysStr.toIntOrNull() ?: 0
        DateTimeEngine.addOrSubtract(baseMillis, y, m, d, isAddition)
    }
    
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = baseMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    state.selectedDateMillis?.let { baseMillis = it }
                    showPicker = false 
                }) { Text("OK") }
            }
        ) { DatePicker(state = state) }
    }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(modifier = Modifier.fillMaxWidth(), onClick = { showPicker = true }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Base Date", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(DateTimeEngine.formatDate(baseMillis), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterChip(selected = isAddition, onClick = { isAddition = true }, label = { Text("Add") })
            FilterChip(selected = !isAddition, onClick = { isAddition = false }, label = { Text("Subtract") })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = yearsStr, onValueChange = { yearsStr = it }, label = { Text("Years") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            OutlinedTextField(value = monthsStr, onValueChange = { monthsStr = it }, label = { Text("Months") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            OutlinedTextField(value = daysStr, onValueChange = { daysStr = it }, label = { Text("Days") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resulting Date", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(DateTimeEngine.formatDate(resultMillis), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
