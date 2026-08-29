package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.model.AppMode
import com.example.ui.theme.CalculatorTheme
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val favoriteHistoryList by viewModel.favoriteHistoryList.collectAsStateWithLifecycle()

    CalculatorTheme(preset = state.themePreset) {
        val colors = LocalCalculatorColors.current

        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background),
            containerColor = colors.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Top Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title / Brand
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "CALCULATOR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = colors.textSecondary
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(colors.accentCyan)
                            )
                        }

                        // Right action buttons: Scientific Tray, Theme, History
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Scientific toggle (only shown when in Calculator mode)
                            if (state.activeMode == AppMode.STANDARD) {
                                IconButton(
                                    onClick = { viewModel.onToggleScientific() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .testTag("toggle_scientific_btn")
                                ) {
                                    Icon(
                                        imageVector = if (state.isScientificOpen) Icons.Filled.Functions else Icons.Outlined.Functions,
                                        contentDescription = "Toggle Scientific Keypad",
                                        tint = if (state.isScientificOpen) colors.keyEquals else colors.textSecondary
                                    )
                                }
                            }

                            // Theme Selector Button
                            IconButton(
                                onClick = { viewModel.onToggleThemeDialog(true) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("theme_selector_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Palette,
                                    contentDescription = "Change Theme",
                                    tint = colors.textSecondary
                                )
                            }

                            // History Button with badge
                            IconButton(
                                onClick = { viewModel.onToggleHistory(true) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("history_btn")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (historyList.isNotEmpty()) {
                                            Badge(
                                                containerColor = colors.keyEquals,
                                                contentColor = if (colors.isDark) Color.Black else Color.White
                                            ) {
                                                Text(
                                                    text = if (historyList.size > 99) "99+" else "${historyList.size}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.History,
                                        contentDescription = "Calculation History",
                                        tint = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Switcher Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppMode.values().forEach { mode ->
                            val isSelected = state.activeMode == mode
                            Surface(
                                onClick = { viewModel.onSelectMode(mode) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) colors.cardBackground else Color.Transparent,
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(1.dp, colors.keyEquals.copy(alpha = 0.5f))
                                } else null,
                                modifier = Modifier.testTag("mode_${mode.name}")
                            ) {
                                Text(
                                    text = mode.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.keyEquals else colors.textTertiary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colors.background)
            ) {
                AnimatedContent(
                    targetState = state.activeMode,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "modeTransition"
                ) { targetMode ->
                    when (targetMode) {
                        AppMode.STANDARD -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navigationBarsPadding(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Screen Display
                                CalculatorDisplay(
                                    expression = state.expression,
                                    liveResult = state.liveResult,
                                    isEvaluated = state.isEvaluated,
                                    angleMode = state.angleMode,
                                    isScientificActive = state.isScientificOpen,
                                    onToggleAngleMode = { viewModel.onToggleAngleMode() },
                                    modifier = Modifier.weight(1f)
                                )

                                // Scientific Keypad (Expandable)
                                ScientificKeypad(
                                    isOpen = state.isScientificOpen,
                                    isInverse = state.isInverseMode,
                                    angleMode = state.angleMode,
                                    onToggleInverse = { viewModel.onToggleInverse() },
                                    onToggleAngleMode = { viewModel.onToggleAngleMode() },
                                    onFunction = { viewModel.onFunction(it) },
                                    onConstant = { viewModel.onConstant(it) },
                                    onOperator = { viewModel.onOperator(it) },
                                    onDelete = { viewModel.onDelete() }
                                )

                                // Standard Numeric & Operator Keypad
                                CalculatorKeypad(
                                    onDigit = { viewModel.onDigit(it) },
                                    onOperator = { viewModel.onOperator(it) },
                                    onDecimal = { viewModel.onDecimal() },
                                    onClear = { viewModel.onClear() },
                                    onDelete = { viewModel.onDelete() },
                                    onEquals = { viewModel.onEquals() },
                                    onParenthesis = { viewModel.onParenthesis() },
                                    onTogglePlusMinus = { viewModel.onTogglePlusMinus() },
                                    onToggleScientific = { viewModel.onToggleScientific() },
                                    isScientificOpen = state.isScientificOpen
                                )
                            }
                        }

                        AppMode.UNIT_CONVERTER -> {
                            UnitConverterView(
                                category = state.unitCategory,
                                inputValue = state.unitInputValue,
                                fromUnitId = state.fromUnitId,
                                toUnitId = state.toUnitId,
                                onCategorySelected = { viewModel.onUnitCategorySelected(it) },
                                onInputChanged = { viewModel.onUnitInputChanged(it) },
                                onFromUnitSelected = { viewModel.onFromUnitSelected(it) },
                                onToUnitSelected = { viewModel.onToUnitSelected(it) },
                                onSwapUnits = { viewModel.onSwapUnits() },
                                modifier = Modifier.navigationBarsPadding()
                            )
                        }

                        AppMode.TIP_SPLITTER -> {
                            TipCalculatorView(
                                billAmount = state.tipBillAmount,
                                tipPercent = state.tipPercent,
                                customTipPercent = state.tipCustomPercent,
                                peopleCount = state.tipPeopleCount,
                                roundUp = state.tipRoundUp,
                                onBillChanged = { viewModel.onTipBillChanged(it) },
                                onTipPercentSelected = { viewModel.onTipPercentSelected(it) },
                                onCustomTipChanged = { viewModel.onTipCustomPercentChanged(it) },
                                onPeopleChanged = { viewModel.onTipPeopleChanged(it) },
                                onRoundUpToggled = { viewModel.onTipRoundUpToggled(it) },
                                modifier = Modifier.navigationBarsPadding()
                            )
                        }

                        AppMode.DISCOUNT_CALCULATOR -> {
                            DiscountCalculatorView(
                                originalPrice = state.discountOriginalPrice,
                                discountPercent = state.discountPercent,
                                taxPercent = state.discountTaxPercent,
                                onPriceChanged = { viewModel.onDiscountPriceChanged(it) },
                                onDiscountChanged = { viewModel.onDiscountPercentChanged(it) },
                                onTaxChanged = { viewModel.onDiscountTaxChanged(it) },
                                modifier = Modifier.navigationBarsPadding()
                            )
                        }
                    }
                }
            }

            // History Bottom Sheet
            HistoryBottomSheet(
                isOpen = state.isHistoryOpen,
                onDismiss = { viewModel.onToggleHistory(false) },
                historyList = historyList,
                favoriteList = favoriteHistoryList,
                onSelectCalculation = { item, reuseResultOnly ->
                    viewModel.onReuseHistoryItem(item, reuseResultOnly)
                },
                onToggleFavorite = { viewModel.onToggleFavorite(it) },
                onUpdateNote = { item, note -> viewModel.onUpdateHistoryNote(item, note) },
                onDeleteItem = { viewModel.onDeleteHistoryItem(it) },
                onClearAll = { viewModel.onClearAllHistory() }
            )

            // Aesthetic Theme Selector Modal
            ThemeSelectorDialog(
                isOpen = state.isThemeDialogOpen,
                currentPreset = state.themePreset,
                onSelectPreset = { viewModel.onSelectTheme(it) },
                onDismiss = { viewModel.onToggleThemeDialog(false) }
            )
        }
    }
}
