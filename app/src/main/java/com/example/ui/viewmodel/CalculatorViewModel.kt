package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationEntity
import com.example.data.repository.HistoryRepository
import com.example.engine.AngleMode
import com.example.engine.EvaluationResult
import com.example.engine.ExpressionEvaluator
import com.example.engine.UnitCategory
import com.example.engine.UnitConverterEngine
import com.example.ui.model.AppMode
import com.example.ui.model.CalculatorState
import com.example.ui.theme.AppThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt

class CalculatorViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<CalculationEntity>> = historyRepository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteHistoryList: StateFlow<List<CalculationEntity>> = historyRepository.favoriteHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.isEvaluated) {
                digit
            } else {
                state.expression + digit
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onDecimal() {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "0." else state.expression
            val lastNumber = expr.split(Regex("[+\\-×÷%^()]")).lastOrNull() ?: ""
            val newExpr = if (lastNumber.contains(".")) {
                expr // already has decimal point in current token
            } else if (expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("(")) {
                expr + "0."
            } else {
                expr + "."
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            val expr = state.expression
            val newExpr = if (expr.isEmpty()) {
                if (op == "−" || op == "-") "−" else ""
            } else if (state.isEvaluated) {
                val base = state.liveResult.ifBlank { expr }
                base + op
            } else if (isLastCharOperator(expr)) {
                expr.dropLast(1) + op
            } else {
                expr + op
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onParenthesis() {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "" else state.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val shouldOpen = expr.isEmpty() ||
                    isLastCharOperator(expr) ||
                    expr.endsWith("(")

            val newExpr = if (shouldOpen) {
                expr + "("
            } else if (openCount > closeCount) {
                expr + ")"
            } else {
                expr + "×("
            }

            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onFunction(func: String) {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "" else state.expression
            val newExpr = when (func) {
                "x²" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^2" else "${expr}0^2"
                "x³" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^3" else "${expr}0^3"
                "x^y" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^" else "${expr}0^"
                "x!" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr!" else "${expr}0!"
                "1/x" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "inv($expr)" else "inv("
                "10^x" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr×10^" else "10^"
                "e^x" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr×e^" else "e^"
                "abs" -> "${expr}abs("
                "sqrt", "√" -> "${expr}sqrt("
                "cbrt" -> "${expr}cbrt("
                else -> "$expr$func("
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onConstant(constant: String) {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "" else state.expression
            val newExpr = if (expr.isNotEmpty() && (expr.last().isDigit() || expr.endsWith(")"))) {
                "$expr×$constant"
            } else {
                "$expr$constant"
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onTogglePlusMinus() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) return@update state

            // Wrap last term in negative / invert
            val newExpr = if (expr.startsWith("−(") && expr.endsWith(")")) {
                expr.substring(2, expr.length - 1)
            } else if (expr.startsWith("-(") && expr.endsWith(")")) {
                expr.substring(2, expr.length - 1)
            } else {
                "−($expr)"
            }
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onDelete() {
        _uiState.update { state ->
            if (state.isEvaluated) {
                return@update state.copy(expression = "", liveResult = "", isEvaluated = false)
            }
            val expr = state.expression
            if (expr.isEmpty()) return@update state

            // Check for multi-character functions at the end
            val functions = listOf("sqrt(", "cbrt(", "asin(", "acos(", "atan(", "log2(", "log(", "sin(", "cos(", "tan(", "inv(", "abs(", "ln(")
            var newExpr = expr
            for (f in functions) {
                if (expr.endsWith(f)) {
                    newExpr = expr.dropLast(f.length)
                    break
                }
            }
            if (newExpr == expr) {
                newExpr = expr.dropLast(1)
            }

            state.copy(
                expression = newExpr,
                liveResult = computeLivePreview(newExpr, state.angleMode)
            )
        }
    }

    fun onClear() {
        _uiState.update { state ->
            state.copy(
                expression = "",
                liveResult = "",
                isEvaluated = false
            )
        }
    }

    fun onEquals() {
        val state = _uiState.value
        if (state.expression.isBlank()) return

        val evaluation = ExpressionEvaluator.evaluate(state.expression, state.angleMode)
        when (evaluation) {
            is EvaluationResult.Success -> {
                val formatted = evaluation.formatted
                val category = if (state.isScientificOpen) "Scientific" else "Standard"

                viewModelScope.launch {
                    historyRepository.insert(
                        expression = state.expression,
                        result = formatted,
                        category = category
                    )
                }

                _uiState.update {
                    it.copy(
                        expression = state.expression,
                        liveResult = formatted,
                        isEvaluated = true
                    )
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update {
                    it.copy(
                        liveResult = evaluation.message,
                        isEvaluated = false
                    )
                }
            }
        }
    }

    fun onToggleAngleMode() {
        _uiState.update { state ->
            val newMode = if (state.angleMode == AngleMode.DEGREE) AngleMode.RADIAN else AngleMode.DEGREE
            state.copy(
                angleMode = newMode,
                liveResult = computeLivePreview(state.expression, newMode)
            )
        }
    }

    fun onToggleScientific() {
        _uiState.update { it.copy(isScientificOpen = !it.isScientificOpen) }
    }

    fun onToggleInverse() {
        _uiState.update { it.copy(isInverseMode = !it.isInverseMode) }
    }

    fun onToggleHistory(open: Boolean? = null) {
        _uiState.update { it.copy(isHistoryOpen = open ?: !it.isHistoryOpen) }
    }

    fun onToggleThemeDialog(open: Boolean? = null) {
        _uiState.update { it.copy(isThemeDialogOpen = open ?: !it.isThemeDialogOpen) }
    }

    fun onSelectTheme(preset: AppThemePreset) {
        _uiState.update { it.copy(themePreset = preset, isThemeDialogOpen = false) }
    }

    fun onSelectMode(mode: AppMode) {
        _uiState.update { it.copy(activeMode = mode) }
    }

    fun onReuseHistoryItem(item: CalculationEntity, reuseResultOnly: Boolean = false) {
        _uiState.update { state ->
            val expr = if (reuseResultOnly) item.result.replace(",", "") else item.expression
            state.copy(
                expression = expr,
                liveResult = computeLivePreview(expr, state.angleMode),
                isEvaluated = false,
                isHistoryOpen = false,
                activeMode = AppMode.STANDARD
            )
        }
    }

    fun onToggleFavorite(item: CalculationEntity) {
        viewModelScope.launch {
            historyRepository.toggleFavorite(item)
        }
    }

    fun onUpdateHistoryNote(item: CalculationEntity, note: String) {
        viewModelScope.launch {
            historyRepository.updateNote(item, note)
        }
    }

    fun onDeleteHistoryItem(item: CalculationEntity) {
        viewModelScope.launch {
            historyRepository.delete(item)
        }
    }

    fun onClearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    // --- Unit Converter Actions ---
    fun onUnitCategorySelected(category: UnitCategory) {
        val units = UnitConverterEngine.getUnits(category)
        _uiState.update { state ->
            state.copy(
                unitCategory = category,
                fromUnitId = units.firstOrNull()?.id ?: "",
                toUnitId = units.getOrNull(1)?.id ?: units.firstOrNull()?.id ?: ""
            )
        }
    }

    fun onUnitInputChanged(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' || it == '-' }
        _uiState.update { it.copy(unitInputValue = filtered) }
    }

    fun onFromUnitSelected(unitId: String) {
        _uiState.update { it.copy(fromUnitId = unitId) }
    }

    fun onToUnitSelected(unitId: String) {
        _uiState.update { it.copy(toUnitId = unitId) }
    }

    fun onSwapUnits() {
        _uiState.update { state ->
            state.copy(
                fromUnitId = state.toUnitId,
                toUnitId = state.fromUnitId
            )
        }
    }

    // --- Tip & Split Actions ---
    fun onTipBillChanged(bill: String) {
        val filtered = bill.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(tipBillAmount = filtered) }
    }

    fun onTipPercentSelected(percent: Int) {
        _uiState.update { it.copy(tipPercent = percent, tipCustomPercent = "") }
    }

    fun onTipCustomPercentChanged(custom: String) {
        val filtered = custom.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(tipCustomPercent = filtered) }
    }

    fun onTipPeopleChanged(people: Int) {
        if (people in 1..100) {
            _uiState.update { it.copy(tipPeopleCount = people) }
        }
    }

    fun onTipRoundUpToggled(roundUp: Boolean) {
        _uiState.update { it.copy(tipRoundUp = roundUp) }
    }

    // --- Discount Actions ---
    fun onDiscountPriceChanged(price: String) {
        val filtered = price.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(discountOriginalPrice = filtered) }
    }

    fun onDiscountPercentChanged(percent: String) {
        val filtered = percent.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(discountPercent = filtered) }
    }

    fun onDiscountTaxChanged(tax: String) {
        val filtered = tax.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(discountTaxPercent = filtered) }
    }

    private fun isLastCharOperator(expr: String): Boolean {
        if (expr.isEmpty()) return false
        val last = expr.last()
        return last in "+-×÷*−–/%^"
    }

    private fun computeLivePreview(expr: String, angleMode: AngleMode): String {
        if (expr.isBlank() || isLastCharOperator(expr)) return ""
        val result = ExpressionEvaluator.evaluate(expr, angleMode)
        return when (result) {
            is EvaluationResult.Success -> result.formatted
            is EvaluationResult.Error -> ""
        }
    }
}
