package com.example.ui.screens.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateToAiMath: () -> Unit,
    onNavigateToMarket: () -> Unit,
    onNavigateToTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tools = listOf(
        ToolItem("AI Math Assistant", Icons.Default.AutoAwesome, onNavigateToAiMath),
        ToolItem("Live Market Prices", Icons.Default.ShowChart, { onNavigateToMarket() }),
        ToolItem("Unit Converter", Icons.Default.SyncAlt, { onNavigateToTool("unit") }),
        ToolItem("Currency Converter", Icons.Default.AttachMoney, { onNavigateToTool("currency") }),
        ToolItem("Geometry", Icons.Default.Category, { onNavigateToTool("geometry") }),
        ToolItem("Statistics", Icons.Default.BarChart, { onNavigateToTool("statistics") }),
        ToolItem("Financial", Icons.Default.AccountBalance, { onNavigateToTool("financial") }),
        ToolItem("Programmer", Icons.Default.Code, { onNavigateToTool("programmer") }),
        ToolItem("Date & Time", Icons.Default.DateRange, { onNavigateToTool("datetime") })
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Math Tools", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.padding(padding).fillMaxSize()
        ) {
            items(tools) { tool ->
                ToolCard(tool = tool)
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { tool.onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = tool.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class ToolItem(val name: String, val icon: ImageVector, val onClick: () -> Unit)
