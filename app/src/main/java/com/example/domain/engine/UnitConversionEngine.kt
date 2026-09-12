package com.example.domain.engine

enum class Category { LENGTH, MASS, TEMPERATURE, TIME }

data class Unit(val id: String, val name: String, val symbol: String, val category: Category)

interface ConversionStrategy {
    val category: Category
    val units: List<Unit>
    fun convert(value: Double, fromUnitId: String, toUnitId: String): Double
}

object LengthConverter : ConversionStrategy {
    override val category = Category.LENGTH
    override val units = listOf(
        Unit("m", "Meter", "m", Category.LENGTH),
        Unit("km", "Kilometer", "km", Category.LENGTH),
        Unit("cm", "Centimeter", "cm", Category.LENGTH),
        Unit("mm", "Millimeter", "mm", Category.LENGTH),
        Unit("mi", "Mile", "mi", Category.LENGTH),
        Unit("yd", "Yard", "yd", Category.LENGTH),
        Unit("ft", "Foot", "ft", Category.LENGTH),
        Unit("in", "Inch", "in", Category.LENGTH)
    )

    override fun convert(value: Double, fromUnitId: String, toUnitId: String): Double {
        val baseValue = value * getFactor(fromUnitId)
        return baseValue / getFactor(toUnitId)
    }

    private fun getFactor(unitId: String): Double = when (unitId) {
        "m" -> 1.0
        "km" -> 1000.0
        "cm" -> 0.01
        "mm" -> 0.001
        "mi" -> 1609.344
        "yd" -> 0.9144
        "ft" -> 0.3048
        "in" -> 0.0254
        else -> 1.0
    }
}

object MassConverter : ConversionStrategy {
    override val category = Category.MASS
    override val units = listOf(
        Unit("kg", "Kilogram", "kg", Category.MASS),
        Unit("g", "Gram", "g", Category.MASS),
        Unit("mg", "Milligram", "mg", Category.MASS),
        Unit("lb", "Pound", "lb", Category.MASS),
        Unit("oz", "Ounce", "oz", Category.MASS)
    )

    override fun convert(value: Double, fromUnitId: String, toUnitId: String): Double {
        val baseValue = value * getFactor(fromUnitId)
        return baseValue / getFactor(toUnitId)
    }

    private fun getFactor(unitId: String): Double = when (unitId) {
        "kg" -> 1.0
        "g" -> 0.001
        "mg" -> 0.000001
        "lb" -> 0.45359237
        "oz" -> 0.02834952
        else -> 1.0
    }
}

object TimeConverter : ConversionStrategy {
    override val category = Category.TIME
    override val units = listOf(
        Unit("s", "Second", "s", Category.TIME),
        Unit("min", "Minute", "min", Category.TIME),
        Unit("h", "Hour", "h", Category.TIME),
        Unit("d", "Day", "d", Category.TIME)
    )

    override fun convert(value: Double, fromUnitId: String, toUnitId: String): Double {
        val baseValue = value * getFactor(fromUnitId)
        return baseValue / getFactor(toUnitId)
    }

    private fun getFactor(unitId: String): Double = when (unitId) {
        "s" -> 1.0
        "min" -> 60.0
        "h" -> 3600.0
        "d" -> 86400.0
        else -> 1.0
    }
}

object TemperatureConverter : ConversionStrategy {
    override val category = Category.TEMPERATURE
    override val units = listOf(
        Unit("c", "Celsius", "°C", Category.TEMPERATURE),
        Unit("f", "Fahrenheit", "°F", Category.TEMPERATURE),
        Unit("k", "Kelvin", "K", Category.TEMPERATURE)
    )

    override fun convert(value: Double, fromUnitId: String, toUnitId: String): Double {
        if (fromUnitId == toUnitId) return value
        val celsius = when (fromUnitId) {
            "c" -> value
            "f" -> (value - 32) * 5.0 / 9.0
            "k" -> value - 273.15
            else -> value
        }
        return when (toUnitId) {
            "c" -> celsius
            "f" -> (celsius * 9.0 / 5.0) + 32
            "k" -> celsius + 273.15
            else -> celsius
        }
    }
}

object UnitConverterFactory {
    private val strategies = mapOf(
        Category.LENGTH to LengthConverter,
        Category.MASS to MassConverter,
        Category.TEMPERATURE to TemperatureConverter,
        Category.TIME to TimeConverter
    )

    fun getStrategy(category: Category): ConversionStrategy {
        return strategies[category] ?: throw IllegalArgumentException("Unsupported category")
    }
}

object UnitConversionEngine {
    val units: List<Unit> by lazy {
        Category.values().flatMap { category ->
            UnitConverterFactory.getStrategy(category).units
        }
    }

    fun getUnitsByCategory(category: Category): List<Unit> {
        return UnitConverterFactory.getStrategy(category).units
    }

    fun convert(value: Double, fromUnitId: String, toUnitId: String): Double {
        val fromUnit = units.find { it.id == fromUnitId } ?: return Double.NaN
        val toUnit = units.find { it.id == toUnitId } ?: return Double.NaN
        if (fromUnit.category != toUnit.category) return Double.NaN
        
        val strategy = UnitConverterFactory.getStrategy(fromUnit.category)
        return strategy.convert(value, fromUnitId, toUnitId)
    }
}
