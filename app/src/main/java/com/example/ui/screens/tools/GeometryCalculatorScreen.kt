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
import com.example.domain.engine.GeometryEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeometryCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedShape by remember { mutableStateOf("Circle") }
    val shapes = listOf("Circle", "Rectangle", "Square", "Right Triangle", "Sphere", "Cylinder", "Cube")
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Geometry Calculator") },
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
            ScrollableTabRow(
                selectedTabIndex = shapes.indexOf(selectedShape),
                edgePadding = 8.dp
            ) {
                shapes.forEach { shape ->
                    Tab(
                        selected = selectedShape == shape,
                        onClick = { selectedShape = shape },
                        text = { Text(shape) }
                    )
                }
            }
            
            Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                when (selectedShape) {
                    "Circle" -> CircleCalculator()
                    "Rectangle" -> RectangleCalculator()
                    "Square" -> SquareCalculator()
                    "Right Triangle" -> RightTriangleCalculator()
                    "Sphere" -> SphereCalculator()
                    "Cylinder" -> CylinderCalculator()
                    "Cube" -> CubeCalculator()
                }
            }
        }
    }
}

@Composable
fun CircleCalculator() {
    var radius by remember { mutableStateOf("10") }
    val result = remember(radius) {
        val r = radius.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateCircle(r)
    }
    
    GeometryInputOutput(
        inputs = listOf(GeometryInput("Radius", radius) { radius = it }),
        result2D = result
    )
}

@Composable
fun RectangleCalculator() {
    var length by remember { mutableStateOf("10") }
    var width by remember { mutableStateOf("5") }
    val result = remember(length, width) {
        val l = length.toDoubleOrNull() ?: 0.0
        val w = width.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateRectangle(l, w)
    }
    
    GeometryInputOutput(
        inputs = listOf(
            GeometryInput("Length", length) { length = it },
            GeometryInput("Width", width) { width = it }
        ),
        result2D = result
    )
}

@Composable
fun SquareCalculator() {
    var side by remember { mutableStateOf("10") }
    val result = remember(side) {
        val s = side.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateSquare(s)
    }
    
    GeometryInputOutput(
        inputs = listOf(GeometryInput("Side", side) { side = it }),
        result2D = result
    )
}

@Composable
fun RightTriangleCalculator() {
    var base by remember { mutableStateOf("3") }
    var height by remember { mutableStateOf("4") }
    val result = remember(base, height) {
        val b = base.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateRightTriangle(b, h)
    }
    
    GeometryInputOutput(
        inputs = listOf(
            GeometryInput("Base", base) { base = it },
            GeometryInput("Height", height) { height = it }
        ),
        result2D = result
    )
}

@Composable
fun SphereCalculator() {
    var radius by remember { mutableStateOf("10") }
    val result = remember(radius) {
        val r = radius.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateSphere(r)
    }
    
    GeometryInputOutput(
        inputs = listOf(GeometryInput("Radius", radius) { radius = it }),
        result3D = result
    )
}

@Composable
fun CylinderCalculator() {
    var radius by remember { mutableStateOf("5") }
    var height by remember { mutableStateOf("10") }
    val result = remember(radius, height) {
        val r = radius.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateCylinder(r, h)
    }
    
    GeometryInputOutput(
        inputs = listOf(
            GeometryInput("Radius", radius) { radius = it },
            GeometryInput("Height", height) { height = it }
        ),
        result3D = result
    )
}

@Composable
fun CubeCalculator() {
    var side by remember { mutableStateOf("10") }
    val result = remember(side) {
        val s = side.toDoubleOrNull() ?: 0.0
        GeometryEngine.calculateCube(s)
    }
    
    GeometryInputOutput(
        inputs = listOf(GeometryInput("Side", side) { side = it }),
        result3D = result
    )
}

data class GeometryInput(val label: String, val value: String, val onValueChange: (String) -> Unit)

@Composable
fun GeometryInputOutput(
    inputs: List<GeometryInput>,
    result2D: GeometryEngine.ShapeResult.TwoD? = null,
    result3D: GeometryEngine.ShapeResult.ThreeD? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        inputs.forEach { input ->
            OutlinedTextField(
                value = input.value,
                onValueChange = input.onValueChange,
                label = { Text(input.label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (result2D != null) {
                    Text("Area", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = String.format("%,.4f", result2D.area),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Perimeter", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = String.format("%,.4f", result2D.perimeter),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (result3D != null) {
                    Text("Volume", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = String.format("%,.4f", result3D.volume),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Surface Area", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = String.format("%,.4f", result3D.surfaceArea),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
