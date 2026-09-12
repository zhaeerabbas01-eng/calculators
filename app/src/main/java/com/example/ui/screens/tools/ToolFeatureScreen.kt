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
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.PI
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolFeatureScreen(
    toolType: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputValue by remember { mutableStateOf("") }
    var resultValue by remember { mutableStateOf("") }

    val title = when(toolType) {
        "unit" -> "Length Converter (Miles to Km)"
        "currency" -> "Currency (USD to EUR)"
        "geometry" -> "Circle Area (Radius -> Area)"
        "statistics" -> "Average (Comma separated)"
        "financial" -> "Simple Interest (P*10%*1yr)"
        "programmer" -> "DEC to BIN/HEX"
        "datetime" -> "Unix to Date"
        else -> "Tool"
    }

    LaunchedEffect(inputValue, toolType) {
        try {
            if (inputValue.isBlank()) {
                resultValue = ""
                return@LaunchedEffect
            }
            resultValue = when(toolType) {
                "unit" -> {
                    val miles = inputValue.toDouble()
                    "${miles * 1.60934} Km"
                }
                "currency" -> {
                    val usd = inputValue.toDouble()
                    "${usd * 0.92} EUR" // Static placeholder rate
                }
                "geometry" -> {
                    val r = inputValue.toDouble()
                    "${PI * r * r}"
                }
                "statistics" -> {
                    val nums = inputValue.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                    if (nums.isNotEmpty()) "${nums.average()}" else "Invalid Input"
                }
                "financial" -> {
                    val p = inputValue.toDouble()
                    "${p * 0.10 * 1} Interest"
                }
                "programmer" -> {
                    val dec = inputValue.toLong()
                    "BIN: ${java.lang.Long.toBinaryString(dec)}\nHEX: ${java.lang.Long.toHexString(dec).uppercase()}"
                }
                "datetime" -> {
                    val unix = inputValue.toLong()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    sdf.format(Date(unix * 1000))
                }
                else -> ""
            }
        } catch (e: Exception) {
            resultValue = "Error"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            val kbType = if (toolType == "statistics") KeyboardType.Text else KeyboardType.Number
            
            Text("Input", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = kbType),
                placeholder = { Text("Enter value...") }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Result", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = resultValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
