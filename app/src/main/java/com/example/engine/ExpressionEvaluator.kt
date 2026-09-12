package com.example.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

class ExpressionEvaluator {
    private val mc = MathContext(32, RoundingMode.HALF_UP)

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEGREE): BigDecimal {
        val sanitized = expression.replace(" ", "").replace("×", "*").replace("÷", "/")
        return Parser(sanitized, angleMode, mc).parse()
    }

    enum class AngleMode {
        DEGREE, RADIAN, GRADIAN
    }

    private class Parser(val expression: String, val angleMode: AngleMode, val mc: MathContext) {
        var pos = -1
        var ch = -1

        fun nextChar() {
            ch = if (++pos < expression.length) expression[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): BigDecimal {
            nextChar()
            val x = parseExpression()
            if (pos < expression.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        fun parseExpression(): BigDecimal {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) x = x.add(parseTerm(), mc)
                else if (eat('-'.code)) x = x.subtract(parseTerm(), mc)
                else return x
            }
        }

        fun parseTerm(): BigDecimal {
            var x = parseFactor()
            while (true) {
                if (eat('*'.code)) x = x.multiply(parseFactor(), mc)
                else if (eat('/'.code)) {
                    val divisor = parseFactor()
                    if (divisor.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Cannot divide by zero.")
                    x = x.divide(divisor, mc)
                }
                else if (eat('m'.code) && eat('o'.code) && eat('d'.code)) {
                    val divisor = parseFactor()
                    if (divisor.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Cannot modulo by zero.")
                    x = x.remainder(divisor, mc)
                }
                else return x
            }
        }

        fun parseFactor(): BigDecimal {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return parseFactor().negate(mc)

            var x: BigDecimal
            val startPos = this.pos

            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) {
                while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code || ch == 'E'.code || ch == 'e'.code) {
                    if ((ch == 'E'.code || ch == 'e'.code) && pos + 1 < expression.length) {
                        val next = expression[pos+1].code
                        if (next == '+'.code || next == '-'.code) {
                            nextChar() 
                            nextChar() 
                        }
                    }
                    nextChar()
                }
                x = BigDecimal(expression.substring(startPos, this.pos))
            } else if (ch >= 'a'.code && ch <= 'z'.code || ch >= 'A'.code && ch <= 'Z'.code || ch == '√'.code || ch == '∛'.code || ch == 'π'.code) {
                while (ch >= 'a'.code && ch <= 'z'.code || ch >= 'A'.code && ch <= 'Z'.code || ch >= '0'.code && ch <= '9'.code || ch == '√'.code || ch == '∛'.code || ch == 'π'.code) nextChar()
                val func = expression.substring(startPos, this.pos)

                if (func == "pi" || func == "π") return BigDecimal(Math.PI)
                if (func == "e") return BigDecimal(Math.E)

                x = parseFactor()

                val angleMultiplier = when(angleMode) {
                    AngleMode.RADIAN -> 1.0
                    AngleMode.DEGREE -> Math.PI / 180.0
                    AngleMode.GRADIAN -> Math.PI / 200.0
                }

                x = when (func) {
                    "sqrt", "√" -> BigDecimal(sqrt(x.toDouble()))
                    "cbrt", "∛" -> BigDecimal(Math.cbrt(x.toDouble()))
                    "sin" -> BigDecimal(sin(x.toDouble() * angleMultiplier))
                    "cos" -> BigDecimal(cos(x.toDouble() * angleMultiplier))
                    "tan" -> BigDecimal(tan(x.toDouble() * angleMultiplier))
                    "asin" -> BigDecimal(asin(x.toDouble()) / angleMultiplier)
                    "acos" -> BigDecimal(acos(x.toDouble()) / angleMultiplier)
                    "atan" -> BigDecimal(atan(x.toDouble()) / angleMultiplier)
                    "sinh" -> BigDecimal(sinh(x.toDouble()))
                    "cosh" -> BigDecimal(cosh(x.toDouble()))
                    "tanh" -> BigDecimal(tanh(x.toDouble()))
                    "log", "log10" -> BigDecimal(log10(x.toDouble()))
                    "ln" -> BigDecimal(ln(x.toDouble()))
                    "abs" -> x.abs(mc)
                    else -> throw RuntimeException("Unknown function: $func")
                }
            } else {
                throw RuntimeException("Unexpected character: " + ch.toChar())
            }

            while (true) {
                if (eat('^'.code)) {
                    val exp = parseFactor()
                    x = BigDecimal(x.toDouble().pow(exp.toDouble()))
                } else if (eat('!'.code)) {
                    var n = x.toInt()
                    if (n < 0) throw ArithmeticException("Factorial of negative number")
                    var fact = BigDecimal.ONE
                    for (i in 2..n) fact = fact.multiply(BigDecimal(i))
                    x = fact
                } else if (eat('%'.code)) {
                    x = x.divide(BigDecimal(100), mc)
                } else if (eat('²'.code)) {
                    x = x.multiply(x, mc)
                } else if (eat('³'.code)) {
                    x = x.multiply(x, mc).multiply(x, mc)
                } else if (eat('⁻'.code) && eat('¹'.code)) {
                    if (x.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Cannot divide by zero.")
                    x = BigDecimal.ONE.divide(x, mc)
                } else {
                    break
                }
            }

            return x
        }
    }
}
