package com.example.ui.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ExpressionEvaluator

import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    isDarkTheme: Boolean = true,
    onThemeToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val angleMode by viewModel.angleMode.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Theme Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onThemeToggle() }
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Display section
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression.ifEmpty { "0" },
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result.ifEmpty { " " },
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        var isScientific by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isScientific) {
                val modeStr = when(angleMode) {
                    ExpressionEvaluator.AngleMode.DEGREE -> "DEG"
                    ExpressionEvaluator.AngleMode.RADIAN -> "RAD"
                    ExpressionEvaluator.AngleMode.GRADIAN -> "GRAD"
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.clickable { viewModel.onAction(CalculatorAction.ToggleAngleMode) }
                ) {
                    Text(
                        text = modeStr,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            FilledTonalButton(
                onClick = { isScientific = !isScientific },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    imageVector = if (isScientific) Icons.Default.Calculate else Icons.Default.Calculate, // Or some suitable icon
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isScientific) "Basic Mode" else "Scientific Mode", fontWeight = FontWeight.Bold)
            }
        }

        // Keypad section
        Box(modifier = Modifier.weight(1.5f)) {
            if (isScientific) {
                ScientificKeypad(viewModel = viewModel)
            } else {
                BasicKeypad(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BasicKeypad(viewModel: CalculatorViewModel) {
    val buttonSpacing = 12.dp
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        val rows = listOf(
            listOf("AC", "⌫", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+")
        )
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                row.forEach { symbol ->
                    CalculatorButton(
                        symbol = symbol,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (symbol) {
                                "AC" -> viewModel.onAction(CalculatorAction.Clear)
                                "%" -> viewModel.onAction(CalculatorAction.Operator("%"))
                                "÷" -> viewModel.onAction(CalculatorAction.Operator("÷"))
                                "×" -> viewModel.onAction(CalculatorAction.Operator("×"))
                                "-" -> viewModel.onAction(CalculatorAction.Operator("-"))
                                "+" -> viewModel.onAction(CalculatorAction.Operator("+"))
                                "⌫" -> viewModel.onAction(CalculatorAction.Delete)
                                else -> viewModel.onAction(CalculatorAction.Number(symbol.toInt()))
                            }
                        }
                    )
                }
            }
        }
        
        // Bottom row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                symbol = "0",
                modifier = Modifier.weight(2f).fillMaxHeight(),
                onClick = { viewModel.onAction(CalculatorAction.Number(0)) }
            )
            CalculatorButton(
                symbol = ".",
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = { viewModel.onAction(CalculatorAction.Decimal) }
            )
            CalculatorButton(
                symbol = "=",
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = { viewModel.onAction(CalculatorAction.Calculate) }
            )
        }
    }
}

@Composable
fun ScientificKeypad(viewModel: CalculatorViewModel) {
    val buttonSpacing = 8.dp
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        val rows = listOf(
            listOf("(", ")", "sin", "cos", "tan"),
            listOf("mod", "asin", "acos", "atan", "abs"),
            listOf("sinh", "cosh", "tanh", "π", "e"),
            listOf("log", "ln", "x²", "√", "∛"),
            listOf("!", "^", "÷", "×", "-")
        )
        val basicRow1 = listOf("7", "8", "9", "+")
        val basicRow2 = listOf("4", "5", "6", ".")
        val basicRow3 = listOf("1", "2", "3", "=")
        val basicRow4 = listOf("AC", "0", "⌫", "")
        
        val allRows = rows + listOf(basicRow1, basicRow2, basicRow3, basicRow4)

        allRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                row.forEach { symbol ->
                    if (symbol.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        CalculatorButton(
                            symbol = symbol,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (symbol) {
                                    "AC" -> viewModel.onAction(CalculatorAction.Clear)
                                    "⌫" -> viewModel.onAction(CalculatorAction.Delete)
                                    "=" -> viewModel.onAction(CalculatorAction.Calculate)
                                    "." -> viewModel.onAction(CalculatorAction.Decimal)
                                    "÷" -> viewModel.onAction(CalculatorAction.Operator("÷"))
                                    "×" -> viewModel.onAction(CalculatorAction.Operator("×"))
                                    "-" -> viewModel.onAction(CalculatorAction.Operator("-"))
                                    "+" -> viewModel.onAction(CalculatorAction.Operator("+"))
                                    "(" -> viewModel.onAction(CalculatorAction.Function("("))
                                    ")" -> viewModel.onAction(CalculatorAction.Function(")"))
                                    "x²" -> viewModel.onAction(CalculatorAction.Operator("²"))
                                    "^" -> viewModel.onAction(CalculatorAction.Operator("^"))
                                    "!" -> viewModel.onAction(CalculatorAction.Operator("!"))
                                    "π" -> viewModel.onAction(CalculatorAction.Function("π"))
                                    "e" -> viewModel.onAction(CalculatorAction.Function("e"))
                                    "mod" -> viewModel.onAction(CalculatorAction.Operator("mod"))
                                    "abs" -> viewModel.onAction(CalculatorAction.Function("abs"))
                                    "0","1","2","3","4","5","6","7","8","9" -> viewModel.onAction(CalculatorAction.Number(symbol.toInt()))
                                    else -> viewModel.onAction(CalculatorAction.Function(symbol))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryButton(symbol: String, onClick: () -> Unit) {
    Text(
        text = symbol,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOperator = symbol in listOf("÷", "×", "-", "+", "=")
    val isAction = symbol in listOf("AC", "()", "%", "⌫")
    
    val containerColor = when {
        symbol == "=" -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.primaryContainer
        isAction -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    
    val contentColor = when {
        symbol == "=" -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.onPrimaryContainer
        isAction -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick)
    ) {
        if (symbol == "⌫") {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = contentColor
            )
        } else {
            Text(
                text = symbol,
                fontSize = if (symbol.length > 2) 20.sp else 28.sp,
                fontWeight = if (isOperator || symbol == "=") FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
