package com.example.domain.engine

object ProgrammerEngine {

    fun convertBase(value: String, fromBase: Int, toBase: Int): String {
        return try {
            val decimalValue = value.toLong(fromBase)
            decimalValue.toString(toBase).uppercase()
        } catch (e: Exception) {
            "Error"
        }
    }
    
    fun calculateBitwise(a: String, b: String, base: Int, operation: BitwiseOp): String {
        return try {
            val valA = a.toLong(base)
            val valB = b.toLong(base)
            val result = when (operation) {
                BitwiseOp.AND -> valA and valB
                BitwiseOp.OR -> valA or valB
                BitwiseOp.XOR -> valA xor valB
                BitwiseOp.SHL -> valA shl valB.toInt()
                BitwiseOp.SHR -> valA shr valB.toInt()
            }
            result.toString(base).uppercase()
        } catch (e: Exception) {
            "Error"
        }
    }
    
    fun calculateBitwiseNot(a: String, base: Int): String {
        return try {
            val valA = a.toLong(base)
            val result = valA.inv()
            result.toString(base).uppercase()
        } catch (e: Exception) {
            "Error"
        }
    }

    enum class BitwiseOp {
        AND, OR, XOR, SHL, SHR
    }
}
