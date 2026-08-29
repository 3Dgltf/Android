package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ConversionUnit
import com.example.engine.UnitCategory
import com.example.engine.UnitConverterEngine
import com.example.ui.theme.LocalCalculatorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterView(
    category: UnitCategory,
    inputValue: String,
    fromUnitId: String,
    toUnitId: String,
    onCategorySelected: (UnitCategory) -> Unit,
    onInputChanged: (String) -> Unit,
    onFromUnitSelected: (String) -> Unit,
    onToUnitSelected: (String) -> Unit,
    onSwapUnits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val units = remember(category) { UnitConverterEngine.getUnits(category) }
    val fromUnit = units.find { it.id == fromUnitId } ?: units.firstOrNull() ?: ConversionUnit("", "", "")
    val toUnit = units.find { it.id == toUnitId } ?: units.getOrNull(1) ?: fromUnit

    val inputNum = inputValue.toDoubleOrNull() ?: 0.0
    val convertedResult = remember(inputNum, fromUnit, toUnit, category) {
        UnitConverterEngine.convert(inputNum, fromUnit, toUnit, category)
    }
    val formattedResult = remember(convertedResult) {
        UnitConverterEngine.formatResult(convertedResult)
    }

    var showFromUnitPicker by remember { mutableStateOf(false) }
    var showToUnitPicker by remember { mutableStateOf(false) }
    var isSwappedAnimation by remember { mutableStateOf(false) }
    val swapRotation by animateFloatAsState(targetValue = if (isSwappedAnimation) 180f else 0f, label = "swap")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UnitCategory.values().forEach { cat ->
                    val isSelected = cat == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(cat) },
                        label = {
                            Text(
                                text = cat.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = getCategoryIcon(cat),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.keyEquals,
                            selectedLabelColor = if (colors.isDark) Color.Black else Color.White,
                            selectedLeadingIconColor = if (colors.isDark) Color.Black else Color.White,
                            containerColor = colors.cardBackground,
                            labelColor = colors.textSecondary,
                            iconColor = colors.textSecondary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("unit_cat_${cat.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conversion Cards Section
            // From Unit Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = colors.cardBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FROM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textTertiary
                        )

                        Surface(
                            onClick = { showFromUnitPicker = true },
                            shape = RoundedCornerShape(10.dp),
                            color = colors.keyFunction.copy(alpha = 0.5f),
                            modifier = Modifier.testTag("select_from_unit")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${fromUnit.name} (${fromUnit.symbol})",
                                    color = colors.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (inputValue.isEmpty()) "0" else inputValue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.testTag("unit_input_value")
                    )
                }
            }

            // Swap Button Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        isSwappedAnimation = !isSwappedAnimation
                        onSwapUnits()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.keyEquals)
                        .rotate(swapRotation)
                        .testTag("swap_units_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap units",
                        tint = if (colors.isDark) Color.Black else Color.White
                    )
                }
            }

            // To Unit Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = colors.cardBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textTertiary
                        )

                        Surface(
                            onClick = { showToUnitPicker = true },
                            shape = RoundedCornerShape(10.dp),
                            color = colors.keyFunction.copy(alpha = 0.5f),
                            modifier = Modifier.testTag("select_to_unit")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${toUnit.name} (${toUnit.symbol})",
                                    color = colors.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = formattedResult,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.keyEquals,
                        modifier = Modifier.testTag("unit_result_value")
                    )
                }
            }
        }

        // Built-in Converter Numpad
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = colors.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UnitKey(text = "7", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "7") }
                    UnitKey(text = "8", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "8") }
                    UnitKey(text = "9", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "9") }
                    UnitKey(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        keyType = KeyType.FUNCTION,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (inputValue.isNotEmpty()) onInputChanged(inputValue.dropLast(1))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UnitKey(text = "4", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "4") }
                    UnitKey(text = "5", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "5") }
                    UnitKey(text = "6", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "6") }
                    UnitKey(text = "AC", keyType = KeyType.FUNCTION, modifier = Modifier.weight(1f)) { onInputChanged("") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UnitKey(text = "1", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "1") }
                    UnitKey(text = "2", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "2") }
                    UnitKey(text = "3", modifier = Modifier.weight(1f)) { onInputChanged(inputValue + "3") }
                    UnitKey(text = "±", keyType = KeyType.FUNCTION, modifier = Modifier.weight(1f)) {
                        if (inputValue.startsWith("-")) onInputChanged(inputValue.removePrefix("-"))
                        else if (inputValue.isNotEmpty() && inputValue != "0") onInputChanged("-$inputValue")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UnitKey(text = "0", modifier = Modifier.weight(2f)) { onInputChanged(if (inputValue == "0") "0" else inputValue + "0") }
                    UnitKey(text = ".", modifier = Modifier.weight(1f)) {
                        if (!inputValue.contains(".")) onInputChanged(if (inputValue.isEmpty()) "0." else "$inputValue.")
                    }
                    UnitKey(
                        icon = Icons.Default.Check,
                        keyType = KeyType.EQUALS,
                        modifier = Modifier.weight(1f)
                    ) { /* dismiss or confirm */ }
                }
            }
        }
    }

    // From Unit Picker Sheet
    if (showFromUnitPicker) {
        UnitPickerSheet(
            title = "Select Source Unit",
            units = units,
            selectedUnitId = fromUnitId,
            onSelect = {
                onFromUnitSelected(it.id)
                showFromUnitPicker = false
            },
            onDismiss = { showFromUnitPicker = false }
        )
    }

    // To Unit Picker Sheet
    if (showToUnitPicker) {
        UnitPickerSheet(
            title = "Select Target Unit",
            units = units,
            selectedUnitId = toUnitId,
            onSelect = {
                onToUnitSelected(it.id)
                showToUnitPicker = false
            },
            onDismiss = { showToUnitPicker = false }
        )
    }
}

@Composable
private fun UnitKey(
    text: String = "",
    icon: ImageVector? = null,
    keyType: KeyType = KeyType.DIGIT,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CalcKey(
        text = text,
        icon = icon,
        keyType = keyType,
        modifier = modifier.heightIn(min = 52.dp),
        testTag = "unit_key_${text.ifEmpty { "icon" }}",
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitPickerSheet(
    title: String,
    units: List<ConversionUnit>,
    selectedUnitId: String,
    onSelect: (ConversionUnit) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalCalculatorColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        contentColor = colors.textPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            units.forEach { unit ->
                val isSelected = unit.id == selectedUnitId
                Surface(
                    onClick = { onSelect(unit) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) colors.cardBackground else Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = unit.name,
                                color = colors.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = unit.symbol,
                                color = colors.textTertiary,
                                fontSize = 13.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = colors.keyEquals
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun getCategoryIcon(category: UnitCategory): ImageVector {
    return when (category) {
        UnitCategory.LENGTH -> Icons.Default.Straighten
        UnitCategory.MASS -> Icons.Default.FitnessCenter
        UnitCategory.TEMPERATURE -> Icons.Default.Thermostat
        UnitCategory.VOLUME -> Icons.Default.Opacity
        UnitCategory.AREA -> Icons.Default.SquareFoot
        UnitCategory.SPEED -> Icons.Default.Speed
        UnitCategory.DATA -> Icons.Default.Storage
        UnitCategory.TIME -> Icons.Default.Schedule
    }
}
