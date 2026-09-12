package com.example.domain.engine

import kotlin.math.pow

object FinanceEngine {
    
    data class EmiResult(
        val monthlyEmi: Double,
        val totalPrincipal: Double,
        val totalInterest: Double,
        val totalPayment: Double
    )

    fun calculateEmi(principal: Double, annualRate: Double, tenureMonths: Int): EmiResult {
        if (principal <= 0 || annualRate <= 0 || tenureMonths <= 0) {
            return EmiResult(0.0, principal, 0.0, principal)
        }
        val r = annualRate / 12 / 100
        val n = tenureMonths.toDouble()
        val emi = principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
        val totalPayment = emi * n
        val totalInterest = totalPayment - principal
        
        return EmiResult(emi, principal, totalInterest, totalPayment)
    }

    data class CompoundInterestResult(
        val initialInvestment: Double,
        val finalBalance: Double,
        val totalInterest: Double
    )

    fun calculateCompoundInterest(
        principal: Double,
        annualRate: Double,
        years: Double,
        compoundsPerYear: Int
    ): CompoundInterestResult {
        if (principal <= 0 || annualRate < 0 || years <= 0 || compoundsPerYear <= 0) {
            return CompoundInterestResult(principal, principal, 0.0)
        }
        val r = annualRate / 100
        val n = compoundsPerYear
        val t = years
        val amount = principal * (1 + r / n).pow(n * t)
        val interest = amount - principal
        
        return CompoundInterestResult(principal, amount, interest)
    }

    data class SimpleInterestResult(
        val principal: Double,
        val totalAmount: Double,
        val interest: Double
    )

    fun calculateSimpleInterest(principal: Double, annualRate: Double, years: Double): SimpleInterestResult {
        if (principal <= 0 || annualRate < 0 || years <= 0) {
            return SimpleInterestResult(principal, principal, 0.0)
        }
        val interest = principal * (annualRate / 100) * years
        val amount = principal + interest
        return SimpleInterestResult(principal, amount, interest)
    }
}
