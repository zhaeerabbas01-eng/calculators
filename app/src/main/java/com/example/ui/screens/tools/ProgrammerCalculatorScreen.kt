package com.example.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.ProgrammerEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgrammerCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputValue by remember { mutableStateOf("") }
    var currentBase by remember { mutableStateOf(10) }

    val hex = ProgrammerEngine.convertBase(inputValue, currentBase, 16)
    val dec = ProgrammerEngine.convertBase(inputValue, currentBase, 10)
    val oct = ProgrammerEngine.convertBase(inputValue, currentBase, 8)
    val bin = ProgrammerEngine.convertBase(inputValue, currentBase, 2)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Programmer Calculator") },
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
            
            // Base selectors
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(selected = currentBase == 16, onClick = { currentBase = 16 }, label = { Text("HEX") })
                FilterChip(selected = currentBase == 10, onClick = { currentBase = 10 }, label = { Text("DEC") })
                FilterChip(selected = currentBase == 8, onClick = { currentBase = 8 }, label = { Text("OCT") })
                FilterChip(selected = currentBase == 2, onClick = { currentBase = 2 }, label = { Text("BIN") })
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Input (Base $currentBase)") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BaseResultRow("HEX", hex, currentBase == 16)
                    BaseResultRow("DEC", dec, currentBase == 10)
                    BaseResultRow("OCT", oct, currentBase == 8)
                    BaseResultRow("BIN", bin, currentBase == 2)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Bitwise Operations (Requires two inputs)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            var bitwiseA by remember { mutableStateOf("") }
            var bitwiseB by remember { mutableStateOf("") }
            var bitwiseOp by remember { mutableStateOf(ProgrammerEngine.BitwiseOp.AND) }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bitwiseA,
                    onValueChange = { bitwiseA = it.uppercase() },
                    modifier = Modifier.weight(1f),
                    label = { Text("A") }
                )
                OutlinedTextField(
                    value = bitwiseB,
                    onValueChange = { bitwiseB = it.uppercase() },
                    modifier = Modifier.weight(1f),
                    label = { Text("B") }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ScrollableTabRow(selectedTabIndex = bitwiseOp.ordinal, edgePadding = 0.dp) {
                ProgrammerEngine.BitwiseOp.values().forEach { op ->
                    Tab(
                        selected = bitwiseOp == op,
                        onClick = { bitwiseOp = op },
                        text = { Text(op.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val bitwiseResult = remember(bitwiseA, bitwiseB, currentBase, bitwiseOp) {
                if (bitwiseA.isNotBlank() && bitwiseB.isNotBlank()) {
                    ProgrammerEngine.calculateBitwise(bitwiseA, bitwiseB, currentBase, bitwiseOp)
                } else {
                    ""
                }
            }
            val bitwiseNotResult = remember(bitwiseA, currentBase) {
                if (bitwiseA.isNotBlank()) {
                    ProgrammerEngine.calculateBitwiseNot(bitwiseA, currentBase)
                } else {
                    ""
                }
            }
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("A ${bitwiseOp.name} B", style = MaterialTheme.typography.labelMedium)
                    Text(bitwiseResult.ifBlank { "---" }, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("NOT A", style = MaterialTheme.typography.labelMedium)
                    Text(bitwiseNotResult.ifBlank { "---" }, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun BaseResultRow(label: String, value: String, isCurrentBase: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (isCurrentBase) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isCurrentBase) FontWeight.Bold else FontWeight.Normal,
            color = if (value == "Error") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}
