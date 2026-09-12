package com.example.ui.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.HistoryRepository
import com.example.domain.model.HistoryItem
import com.example.engine.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CalculatorViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val evaluator = ExpressionEvaluator()
    private val decimalFormat = DecimalFormat("0.##########", DecimalFormatSymbols(Locale.US)).apply {
        maximumFractionDigits = 10
    }

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _angleMode = MutableStateFlow(ExpressionEvaluator.AngleMode.DEGREE)
    val angleMode: StateFlow<ExpressionEvaluator.AngleMode> = _angleMode.asStateFlow()

    private var memoryValue: BigDecimal = BigDecimal.ZERO
    
    private var isCalculated = false

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> append(action.number.toString())
            is CalculatorAction.Operator -> appendOperator(action.operator)
            is CalculatorAction.Decimal -> appendDecimal()
            CalculatorAction.Clear -> clear()
            CalculatorAction.Delete -> delete()
            CalculatorAction.Calculate -> calculate(true)
            is CalculatorAction.Function -> appendFunction(action.func)
            CalculatorAction.ToggleAngleMode -> toggleAngleMode()
            CalculatorAction.MemoryClear -> memoryValue = BigDecimal.ZERO
            CalculatorAction.MemoryRecall -> append(formatNumber(memoryValue))
            CalculatorAction.MemoryAdd -> addToMemory(true)
            CalculatorAction.MemorySubtract -> addToMemory(false)
            CalculatorAction.Parentheses -> handleParentheses()
            CalculatorAction.SaveToHistory -> saveToHistory()
        }
    }

    private fun append(str: String) {
        if (isCalculated) {
            _expression.value = ""
            isCalculated = false
        }
        _expression.value += str
        calculate(false)
    }

    private fun appendOperator(operator: String) {
        if (isCalculated) {
            _expression.value = _result.value
            isCalculated = false
        }
        if (_expression.value.isNotEmpty() && isOperator(_expression.value.last())) {
             _expression.value = _expression.value.dropLast(1) + operator
        } else {
            _expression.value += operator
        }
    }

    private fun isOperator(c: Char) = c == '+' || c == '-' || c == '×' || c == '÷' || c == '*' || c == '/' || c == '^' || c == '%'

    private fun appendDecimal() {
        if (isCalculated) {
            _expression.value = "0."
            isCalculated = false
            return
        }
        val current = _expression.value
        val lastNumberObj = current.split(Regex("[+×÷\\-()^%√∛!]+"))
        val lastNumber = lastNumberObj.lastOrNull() ?: ""
        if (!lastNumber.contains(".")) {
            _expression.value += if (lastNumber.isEmpty()) "0." else "."
        }
    }

    private fun clear() {
        _expression.value = ""
        _result.value = ""
        isCalculated = false
    }

    private fun delete() {
        if (isCalculated) {
            _expression.value = ""
            _result.value = ""
            isCalculated = false
            return
        }
        if (_expression.value.isNotEmpty()) {
            _expression.value = _expression.value.dropLast(1)
            calculate(false)
        }
    }

    private fun calculate(isFinal: Boolean) {
        if (_expression.value.isBlank()) {
            _result.value = ""
            return
        }
        try {
            // Check balanced parentheses, if final, auto-balance
            var exp = _expression.value
            if (isFinal) {
                val openCount = exp.count { it == '(' }
                val closeCount = exp.count { it == ')' }
                if (openCount > closeCount) {
                    exp += ")".repeat(openCount - closeCount)
                    _expression.value = exp
                }
            }
            
            val res = evaluator.evaluate(exp, _angleMode.value)
            val formatted = formatNumber(res)
            _result.value = formatted
            if (isFinal) {
                isCalculated = true
                saveToHistory()
            }
        } catch (e: Exception) {
            if (isFinal) {
                _result.value = "Error"
            } else {
                _result.value = ""
            }
        }
    }
    
    private fun formatNumber(number: BigDecimal): String {
        return decimalFormat.format(number)
    }

    private fun appendFunction(func: String) {
        if (isCalculated) {
            _expression.value = ""
            isCalculated = false
        }
        _expression.value += "$func("
    }

    private fun toggleAngleMode() {
        _angleMode.value = when (_angleMode.value) {
            ExpressionEvaluator.AngleMode.DEGREE -> ExpressionEvaluator.AngleMode.RADIAN
            ExpressionEvaluator.AngleMode.RADIAN -> ExpressionEvaluator.AngleMode.GRADIAN
            ExpressionEvaluator.AngleMode.GRADIAN -> ExpressionEvaluator.AngleMode.DEGREE
        }
        calculate(false)
    }

    private fun addToMemory(isAdd: Boolean) {
        val valueToMemory = if (_result.value.isNotEmpty() && _result.value != "Error") {
            BigDecimal(_result.value.replace(",", ""))
        } else if (_expression.value.isNotEmpty() && _expression.value.toDoubleOrNull() != null) {
            BigDecimal(_expression.value)
        } else return
        
        memoryValue = if (isAdd) memoryValue.add(valueToMemory) else memoryValue.subtract(valueToMemory)
    }

    private fun handleParentheses() {
        if (isCalculated) {
             _expression.value = "("
             isCalculated = false
             return
        }
        val openCount = _expression.value.count { it == '(' }
        val closeCount = _expression.value.count { it == ')' }
        val lastChar = _expression.value.lastOrNull()
        
        if (openCount == closeCount || (lastChar != null && lastChar == '(') || (lastChar != null && isOperator(lastChar))) {
            _expression.value += "("
        } else if (openCount > closeCount) {
            _expression.value += ")"
        }
        calculate(false)
    }

    private fun saveToHistory() {
        val exp = _expression.value
        val res = _result.value
        if (exp.isNotBlank() && res.isNotBlank() && res != "Error" && exp != res) {
            viewModelScope.launch {
                historyRepository.insert(HistoryItem(expression = exp, result = res))
            }
        }
    }

    class Factory(private val historyRepository: HistoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalculatorViewModel(historyRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    data class Function(val func: String) : CalculatorAction()
    object Decimal : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object ToggleAngleMode : CalculatorAction()
    object MemoryClear : CalculatorAction()
    object MemoryRecall : CalculatorAction()
    object MemoryAdd : CalculatorAction()
    object MemorySubtract : CalculatorAction()
    object Parentheses : CalculatorAction()
    object SaveToHistory : CalculatorAction()
}
