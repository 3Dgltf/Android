package com.example

import com.example.engine.AngleMode
import com.example.engine.EvaluationResult
import com.example.engine.ExpressionEvaluator
import com.example.engine.UnitCategory
import com.example.engine.UnitConverterEngine
import org.junit.Assert.*
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun testBasicArithmetic() {
        val res1 = ExpressionEvaluator.evaluate("2+3")
        assertTrue(res1 is EvaluationResult.Success)
        assertEquals("5", (res1 as EvaluationResult.Success).formatted)

        val res2 = ExpressionEvaluator.evaluate("10-4.5")
        assertTrue(res2 is EvaluationResult.Success)
        assertEquals("5.5", (res2 as EvaluationResult.Success).formatted)

        val res3 = ExpressionEvaluator.evaluate("6*7")
        assertTrue(res3 is EvaluationResult.Success)
        assertEquals("42", (res3 as EvaluationResult.Success).formatted)

        val res4 = ExpressionEvaluator.evaluate("15/3")
        assertTrue(res4 is EvaluationResult.Success)
        assertEquals("5", (res4 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testOperatorPrecedenceAndParentheses() {
        val res1 = ExpressionEvaluator.evaluate("2+3*4")
        assertTrue(res1 is EvaluationResult.Success)
        assertEquals("14", (res1 as EvaluationResult.Success).formatted)

        val res2 = ExpressionEvaluator.evaluate("(2+3)*4")
        assertTrue(res2 is EvaluationResult.Success)
        assertEquals("20", (res2 as EvaluationResult.Success).formatted)

        val res3 = ExpressionEvaluator.evaluate("2^3+1")
        assertTrue(res3 is EvaluationResult.Success)
        assertEquals("9", (res3 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testTrigonometricAndScientificFunctions() {
        // Degree mode sin(30) = 0.5
        val resSin = ExpressionEvaluator.evaluate("sin(30)", AngleMode.DEGREE)
        assertTrue(resSin is EvaluationResult.Success)
        assertEquals("0.5", (resSin as EvaluationResult.Success).formatted)

        // cos(60) = 0.5
        val resCos = ExpressionEvaluator.evaluate("cos(60)", AngleMode.DEGREE)
        assertTrue(resCos is EvaluationResult.Success)
        assertEquals("0.5", (resCos as EvaluationResult.Success).formatted)

        // sqrt(16) = 4
        val resSqrt = ExpressionEvaluator.evaluate("sqrt(16)")
        assertTrue(resSqrt is EvaluationResult.Success)
        assertEquals("4", (resSqrt as EvaluationResult.Success).formatted)

        // Factorial 5! = 120
        val resFact = ExpressionEvaluator.evaluate("5!")
        assertTrue(resFact is EvaluationResult.Success)
        assertEquals("120", (resFact as EvaluationResult.Success).formatted)
    }

    @Test
    fun testUnitConverter() {
        val lengthUnits = UnitConverterEngine.getUnits(UnitCategory.LENGTH)
        val meter = lengthUnits.find { it.id == "m" }!!
        val km = lengthUnits.find { it.id == "km" }!!

        val converted = UnitConverterEngine.convert(5000.0, meter, km, UnitCategory.LENGTH)
        assertEquals(5.0, converted, 0.0001)

        // Temperature C to F: 0 C = 32 F
        val tempUnits = UnitConverterEngine.getUnits(UnitCategory.TEMPERATURE)
        val c = tempUnits.find { it.id == "C" }!!
        val f = tempUnits.find { it.id == "F" }!!
        val fahrenheit = UnitConverterEngine.convert(0.0, c, f, UnitCategory.TEMPERATURE)
        assertEquals(32.0, fahrenheit, 0.0001)
    }
}
