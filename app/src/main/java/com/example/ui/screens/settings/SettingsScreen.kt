package com.example.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("App Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { Text("Dark Mode (Default)") }
            )
            Divider()
            ListItem(
                headlineContent = { Text("Haptic Feedback") },
                trailingContent = { Switch(checked = true, onCheckedChange = {}) }
            )
            Divider()
            ListItem(
                headlineContent = { Text("Decimal Precision") },
                supportingContent = { Text("10 digits") }
            )
            Divider()
            ListItem(
                headlineContent = { Text("About CalcX") },
                supportingContent = { Text("Version 1.0.0") }
            )
        }
    }
}
