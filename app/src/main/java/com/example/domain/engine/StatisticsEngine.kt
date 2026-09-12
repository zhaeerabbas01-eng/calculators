package com.example.domain.engine

import kotlin.math.pow
import kotlin.math.sqrt

object StatisticsEngine {
    
    data class StatisticsResult(
        val count: Int,
        val sum: Double,
        val mean: Double,
        val median: Double,
        val mode: List<Double>,
        val variance: Double,
        val standardDeviation: Double,
        val min: Double,
        val max: Double,
        val range: Double
    )

    fun calculateStatistics(data: List<Double>): StatisticsResult? {
        if (data.isEmpty()) return null
        
        val count = data.size
        val sum = data.sum()
        val mean = sum / count
        
        val sorted = data.sorted()
        val median = if (count % 2 == 0) {
            (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        } else {
            sorted[count / 2]
        }
        
        val frequencies = data.groupingBy { it }.eachCount()
        val maxFreq = frequencies.values.maxOrNull() ?: 0
        val mode = frequencies.filter { it.value == maxFreq }.keys.toList().sorted()
        
        val variance = if (count > 1) {
            data.sumOf { (it - mean).pow(2) } / (count - 1)
        } else {
            0.0
        }
        
        val standardDeviation = sqrt(variance)
        val min = sorted.first()
        val max = sorted.last()
        val range = max - min
        
        return StatisticsResult(
            count = count,
            sum = sum,
            mean = mean,
            median = median,
            mode = mode,
            variance = variance,
            standardDeviation = standardDeviation,
            min = min,
            max = max,
            range = range
        )
    }
}
