package com.example.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

enum class AngleMode {
    DEGREE,
    RADIAN
}

sealed class EvaluationResult {
    data class Success(val value: Double, val formatted: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}

object ExpressionEvaluator {

    private val numberFormatSymbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEGREE): EvaluationResult {
        if (expression.isBlank()) {
            return EvaluationResult.Success(0.0, "0")
        }

        try {
            val sanitized = sanitizeExpression(expression)
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) {
                return EvaluationResult.Success(0.0, "0")
            }
            val rpn = infixToRpn(tokens)
            val result = evaluateRpn(rpn, angleMode)

            if (result.isNaN()) {
                return EvaluationResult.Error("Undefined")
            }
            if (result.isInfinite()) {
                return EvaluationResult.Error("Cannot divide by zero")
            }

            val formatted = formatNumber(result)
            return EvaluationResult.Success(result, formatted)
        } catch (e: ArithmeticException) {
            return EvaluationResult.Error(e.message ?: "Math error")
        } catch (e: Exception) {
            return EvaluationResult.Error("Invalid format")
        }
    }

    private fun sanitizeExpression(raw: String): String {
        var expr = raw.trim()
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("–", "-")
            .replace("π", "PI")
            .replace("e", "E")
            .replace("√", "sqrt")

        // Auto-close open parentheses if user didn't close them yet
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        if (openCount > closeCount) {
            expr += ")".repeat(openCount - closeCount)
        }

        return expr
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]

            if (c.isWhitespace()) {
                i++
                continue
            }

            // Numbers: digits and decimal point
            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
                continue
            }

            // Identifiers / Constants / Functions (sin, cos, tan, log, ln, sqrt, etc.)
            if (c.isLetter()) {
                val sb = StringBuilder()
                while (i < len && expr[i].isLetter()) {
                    sb.append(expr[i])
                    i++
                }
                val word = sb.toString()
                tokens.add(word)
                continue
            }

            // Operators and brackets
            if (c in "+-*/^%()!") {
                tokens.add(c.toString())
                i++
                continue
            }

            i++
        }

        // Handle implicit multiplication & unary minus
        val processed = mutableListOf<String>()
        for (idx in tokens.indices) {
            val curr = tokens[idx]
            val prev = if (idx > 0) tokens[idx - 1] else null

            // Unary minus: if '-' is preceded by nothing, an operator, or '(', convert to negative number or unary minus token 'u-'
            if (curr == "-") {
                val isUnary = prev == null || isOperator(prev) || prev == "("
                if (isUnary) {
                    processed.add("u-")
                    continue
                }
            }

            // Implicit multiplication: e.g. 5(3) -> 5 * (3), (2)(3) -> (2) * (3), 3PI -> 3 * PI, 5sin(30) -> 5 * sin(30)
            if (prev != null) {
                val prevIsNumOrConstOrClose = isNumber(prev) || prev == "PI" || prev == "E" || prev == ")" || prev == "!"
                val currIsNumOrConstOrOpenOrFunc = isNumber(curr) || curr == "PI" || curr == "E" || curr == "(" || isFunction(curr)
                if (prevIsNumOrConstOrClose && currIsNumOrConstOrOpenOrFunc && !isOperator(curr) && curr != ")") {
                    processed.add("*")
                }
            }

            processed.add(curr)
        }

        return processed
    }

    private fun isOperator(token: String): Boolean = token in listOf("+", "-", "*", "/", "^", "%", "u-")
    private fun isFunction(token: String): Boolean = token.lowercase() in listOf("sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "log2", "sqrt", "cbrt", "abs", "inv")
    private fun isNumber(token: String): Boolean = token.toDoubleOrNull() != null

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/", "%" -> 2
        "u-" -> 3
        "^" -> 4
        "!" -> 5
        else -> 0
    }

    private fun isRightAssociative(op: String): Boolean = op == "^" || op == "u-"

    private fun infixToRpn(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operatorStack = ArrayDeque<String>()

        for (token in tokens) {
            if (isNumber(token) || token == "PI" || token == "E") {
                output.add(token)
            } else if (isFunction(token)) {
                operatorStack.addFirst(token)
            } else if (token == "!") {
                output.add(token) // postfix operator
            } else if (isOperator(token)) {
                val p1 = precedence(token)
                while (operatorStack.isNotEmpty()) {
                    val top = operatorStack.first()
                    if (isFunction(top)) {
                        output.add(operatorStack.removeFirst())
                    } else if (isOperator(top)) {
                        val p2 = precedence(top)
                        if ((!isRightAssociative(token) && p1 <= p2) || (isRightAssociative(token) && p1 < p2)) {
                            output.add(operatorStack.removeFirst())
                        } else {
                            break
                        }
                    } else {
                        break
                    }
                }
                operatorStack.addFirst(token)
            } else if (token == "(") {
                operatorStack.addFirst(token)
            } else if (token == ")") {
                while (operatorStack.isNotEmpty() && operatorStack.first() != "(") {
                    output.add(operatorStack.removeFirst())
                }
                if (operatorStack.isNotEmpty() && operatorStack.first() == "(") {
                    operatorStack.removeFirst()
                }
                if (operatorStack.isNotEmpty() && isFunction(operatorStack.first())) {
                    output.add(operatorStack.removeFirst())
                }
            }
        }

        while (operatorStack.isNotEmpty()) {
            val op = operatorStack.removeFirst()
            if (op != "(" && op != ")") {
                output.add(op)
            }
        }

        return output
    }

    private fun evaluateRpn(rpn: List<String>, angleMode: AngleMode): Double {
        val stack = ArrayDeque<Double>()

        for (token in rpn) {
            when {
                token == "PI" -> stack.addFirst(Math.PI)
                token == "E" -> stack.addFirst(Math.E)
                isNumber(token) -> stack.addFirst(token.toDouble())
                token == "u-" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand")
                    val a = stack.removeFirst()
                    stack.addFirst(-a)
                }
                token == "!" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand")
                    val n = stack.removeFirst()
                    stack.addFirst(factorial(n))
                }
                isFunction(token) -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing function argument")
                    val a = stack.removeFirst()
                    val res = when (token.lowercase()) {
                        "sin" -> if (angleMode == AngleMode.DEGREE) sin(Math.toRadians(a)) else sin(a)
                        "cos" -> if (angleMode == AngleMode.DEGREE) cos(Math.toRadians(a)) else cos(a)
                        "tan" -> {
                            val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(a) else a
                            if (abs(cos(rad)) < 1e-15) Double.POSITIVE_INFINITY else tan(rad)
                        }
                        "asin" -> {
                            if (a < -1.0 || a > 1.0) throw ArithmeticException("Domain error")
                            val rad = asin(a)
                            if (angleMode == AngleMode.DEGREE) Math.toDegrees(rad) else rad
                        }
                        "acos" -> {
                            if (a < -1.0 || a > 1.0) throw ArithmeticException("Domain error")
                            val rad = acos(a)
                            if (angleMode == AngleMode.DEGREE) Math.toDegrees(rad) else rad
                        }
                        "atan" -> {
                            val rad = atan(a)
                            if (angleMode == AngleMode.DEGREE) Math.toDegrees(rad) else rad
                        }
                        "log" -> {
                            if (a <= 0) throw ArithmeticException("Domain error")
                            log10(a)
                        }
                        "ln" -> {
                            if (a <= 0) throw ArithmeticException("Domain error")
                            ln(a)
                        }
                        "log2" -> {
                            if (a <= 0) throw ArithmeticException("Domain error")
                            ln(a) / ln(2.0)
                        }
                        "sqrt" -> {
                            if (a < 0) throw ArithmeticException("Negative square root")
                            sqrt(a)
                        }
                        "cbrt" -> cbrt(a)
                        "abs" -> abs(a)
                        "inv" -> {
                            if (a == 0.0) throw ArithmeticException("Division by zero")
                            1.0 / a
                        }
                        else -> throw IllegalArgumentException("Unknown function: $token")
                    }
                    stack.addFirst(res)
                }
                isOperator(token) -> {
                    if (stack.size < 2) throw IllegalArgumentException("Missing operand")
                    val b = stack.removeFirst()
                    val a = stack.removeFirst()
                    val res = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (b == 0.0) throw ArithmeticException("Division by zero")
                            a / b
                        }
                        "^" -> a.pow(b)
                        "%" -> a % b
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    }
                    stack.addFirst(res)
                }
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Invalid expression structure")
        }

        return stack.removeFirst()
    }

    private fun factorial(n: Double): Double {
        if (n < 0 || n != floor(n)) throw ArithmeticException("Factorial is only defined for non-negative integers")
        if (n > 170) return Double.POSITIVE_INFINITY
        var result = 1.0
        val count = n.toLong()
        for (i in 2..count) {
            result *= i
        }
        return result
    }

    fun formatNumber(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // For integers or near integers
        if (value == floor(value) && abs(value) < 1e14) {
            val longVal = value.toLong()
            val df = DecimalFormat("#,###", numberFormatSymbols)
            return df.format(longVal)
        }

        // For very large or very small numbers
        if (abs(value) >= 1e12 || (abs(value) > 0 && abs(value) < 1e-6)) {
            val df = DecimalFormat("0.######E0", numberFormatSymbols)
            return df.format(value).replace("E", "e")
        }

        // Standard decimal formatting up to 10 decimal places, trimmed
        val df = DecimalFormat("#,##0.##########", numberFormatSymbols)
        return df.format(value)
    }

    fun formatRawNumber(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        if (value == floor(value) && abs(value) < 1e14) {
            return value.toLong().toString()
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
    }
}
