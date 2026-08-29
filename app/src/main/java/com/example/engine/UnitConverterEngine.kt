package com.example.engine

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class UnitCategory(val title: String) {
    LENGTH("Length"),
    MASS("Mass & Weight"),
    TEMPERATURE("Temperature"),
    VOLUME("Volume"),
    AREA("Area"),
    SPEED("Speed"),
    DATA("Digital Storage"),
    TIME("Time")
}

data class ConversionUnit(
    val id: String,
    val name: String,
    val symbol: String,
    val toBaseFactor: Double = 1.0, // Factor to convert to base unit
    val isTemperature: Boolean = false
)

object UnitConverterEngine {

    private val unitsByCategory = mapOf(
        UnitCategory.LENGTH to listOf(
            ConversionUnit("m", "Meter", "m", 1.0),
            ConversionUnit("km", "Kilometer", "km", 1000.0),
            ConversionUnit("cm", "Centimeter", "cm", 0.01),
            ConversionUnit("mm", "Millimeter", "mm", 0.001),
            ConversionUnit("mi", "Mile", "mi", 1609.344),
            ConversionUnit("yd", "Yard", "yd", 0.9144),
            ConversionUnit("ft", "Foot", "ft", 0.3048),
            ConversionUnit("in", "Inch", "in", 0.0254)
        ),
        UnitCategory.MASS to listOf(
            ConversionUnit("kg", "Kilogram", "kg", 1.0),
            ConversionUnit("g", "Gram", "g", 0.001),
            ConversionUnit("mg", "Milligram", "mg", 0.000001),
            ConversionUnit("lb", "Pound", "lb", 0.45359237),
            ConversionUnit("oz", "Ounce", "oz", 0.02834952),
            ConversionUnit("t", "Metric Ton", "t", 1000.0),
            ConversionUnit("st", "Stone", "st", 6.35029)
        ),
        UnitCategory.TEMPERATURE to listOf(
            ConversionUnit("C", "Celsius", "°C", isTemperature = true),
            ConversionUnit("F", "Fahrenheit", "°F", isTemperature = true),
            ConversionUnit("K", "Kelvin", "K", isTemperature = true)
        ),
        UnitCategory.VOLUME to listOf(
            ConversionUnit("l", "Liter", "L", 1.0),
            ConversionUnit("ml", "Milliliter", "mL", 0.001),
            ConversionUnit("gal", "Gallon (US)", "gal", 3.78541),
            ConversionUnit("qt", "Quart", "qt", 0.946353),
            ConversionUnit("pt", "Pint", "pt", 0.473176),
            ConversionUnit("cup", "Cup", "cup", 0.236588),
            ConversionUnit("floz", "Fluid Ounce", "fl oz", 0.0295735),
            ConversionUnit("m3", "Cubic Meter", "m³", 1000.0)
        ),
        UnitCategory.AREA to listOf(
            ConversionUnit("m2", "Square Meter", "m²", 1.0),
            ConversionUnit("km2", "Square Kilometer", "km²", 1_000_000.0),
            ConversionUnit("ha", "Hectare", "ha", 10_000.0),
            ConversionUnit("ac", "Acre", "ac", 4046.86),
            ConversionUnit("sqft", "Square Foot", "sq ft", 0.092903),
            ConversionUnit("sqmi", "Square Mile", "sq mi", 2_589_988.11)
        ),
        UnitCategory.SPEED to listOf(
            ConversionUnit("mps", "Meter/second", "m/s", 1.0),
            ConversionUnit("kmh", "Kilometer/hour", "km/h", 0.277778),
            ConversionUnit("mph", "Mile/hour", "mph", 0.44704),
            ConversionUnit("knot", "Knot", "kn", 0.514444),
            ConversionUnit("fps", "Foot/second", "ft/s", 0.3048)
        ),
        UnitCategory.DATA to listOf(
            ConversionUnit("b", "Byte", "B", 1.0),
            ConversionUnit("kb", "Kilobyte", "KB", 1024.0),
            ConversionUnit("mb", "Megabyte", "MB", 1024.0 * 1024.0),
            ConversionUnit("gb", "Gigabyte", "GB", 1024.0 * 1024.0 * 1024.0),
            ConversionUnit("tb", "Terabyte", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0),
            ConversionUnit("pb", "Petabyte", "PB", 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0)
        ),
        UnitCategory.TIME to listOf(
            ConversionUnit("s", "Second", "s", 1.0),
            ConversionUnit("min", "Minute", "min", 60.0),
            ConversionUnit("h", "Hour", "hr", 3600.0),
            ConversionUnit("d", "Day", "d", 86400.0),
            ConversionUnit("wk", "Week", "wk", 604800.0),
            ConversionUnit("mo", "Month (30d)", "mo", 2592000.0),
            ConversionUnit("yr", "Year (365d)", "yr", 31536000.0)
        )
    )

    fun getUnits(category: UnitCategory): List<ConversionUnit> {
        return unitsByCategory[category] ?: emptyList()
    }

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit, category: UnitCategory): Double {
        if (from.id == to.id) return value

        if (category == UnitCategory.TEMPERATURE) {
            // Convert to Celsius first
            val celsius = when (from.id) {
                "C" -> value
                "F" -> (value - 32.0) * (5.0 / 9.0)
                "K" -> value - 273.15
                else -> value
            }

            // Convert Celsius to Target
            return when (to.id) {
                "C" -> celsius
                "F" -> (celsius * (9.0 / 5.0)) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        // Standard linear conversion: value * fromFactor / toFactor
        val baseValue = value * from.toBaseFactor
        return baseValue / to.toBaseFactor
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "0"
        if (value.isInfinite()) return "∞"
        if (value == 0.0) return "0"

        val symbols = DecimalFormatSymbols(Locale.US)
        if (kotlin.math.abs(value) >= 1e9 || (kotlin.math.abs(value) > 0 && kotlin.math.abs(value) < 1e-4)) {
            val df = DecimalFormat("0.#####E0", symbols)
            return df.format(value).replace("E", "e")
        }

        val df = DecimalFormat("#,##0.######", symbols)
        return df.format(value)
    }
}
