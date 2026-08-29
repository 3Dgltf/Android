package com.example.ui.model

import com.example.engine.AngleMode
import com.example.engine.UnitCategory
import com.example.ui.theme.AppThemePreset

enum class AppMode(val label: String) {
    STANDARD("Calculator"),
    UNIT_CONVERTER("Converter"),
    TIP_SPLITTER("Tip & Split"),
    DISCOUNT_CALCULATOR("Discount")
}

data class CalculatorState(
    // Calculator core
    val expression: String = "",
    val liveResult: String = "",
    val isEvaluated: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEGREE,
    val isScientificOpen: Boolean = false,
    val isInverseMode: Boolean = false, // 2nd key: sin -> asin, cos -> acos, etc.
    val isHistoryOpen: Boolean = false,
    val isThemeDialogOpen: Boolean = false,
    val themePreset: AppThemePreset = AppThemePreset.SOPHISTICATED_DARK,
    val activeMode: AppMode = AppMode.STANDARD,

    // Unit converter state
    val unitCategory: UnitCategory = UnitCategory.LENGTH,
    val unitInputValue: String = "1",
    val fromUnitId: String = "m",
    val toUnitId: String = "ft",

    // Tip & Split state
    val tipBillAmount: String = "50",
    val tipPercent: Int = 15,
    val tipCustomPercent: String = "",
    val tipPeopleCount: Int = 2,
    val tipRoundUp: Boolean = false,

    // Discount state
    val discountOriginalPrice: String = "100",
    val discountPercent: String = "20",
    val discountTaxPercent: String = "0"
)
