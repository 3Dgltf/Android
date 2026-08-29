package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.ceil

@Composable
fun TipCalculatorView(
    billAmount: String,
    tipPercent: Int,
    customTipPercent: String,
    peopleCount: Int,
    roundUp: Boolean,
    onBillChanged: (String) -> Unit,
    onTipPercentSelected: (Int) -> Unit,
    onCustomTipChanged: (String) -> Unit,
    onPeopleChanged: (Int) -> Unit,
    onRoundUpToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val bill = billAmount.toDoubleOrNull() ?: 0.0
    val effectiveTipPercent = customTipPercent.toDoubleOrNull() ?: tipPercent.toDouble()

    val totalTip = bill * (effectiveTipPercent / 100.0)
    var grandTotal = bill + totalTip
    var perPerson = if (peopleCount > 0) grandTotal / peopleCount else grandTotal
    if (roundUp) {
        perPerson = ceil(perPerson)
        grandTotal = perPerson * peopleCount
    }
    val tipPerPerson = if (peopleCount > 0) (grandTotal - bill) / peopleCount else totalTip

    val currencyFormat = remember {
        DecimalFormat("$#,##0.00", DecimalFormatSymbols(Locale.US))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Total Per Person
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.cardBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(colors.keyEquals.copy(alpha = 0.25f), Color.Transparent),
                            radius = 450f
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOTAL PER PERSON",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currencyFormat.format(perPerson),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.keyEquals,
                        modifier = Modifier.testTag("tip_per_person_total")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakdown chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Total Bill", fontSize = 12.sp, color = colors.textTertiary)
                            Text(
                                text = currencyFormat.format(grandTotal),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(colors.keyFunction.copy(alpha = 0.4f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Total Tip", fontSize = 12.sp, color = colors.textTertiary)
                            Text(
                                text = currencyFormat.format(totalTip),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(colors.keyFunction.copy(alpha = 0.4f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Tip/Person", fontSize = 12.sp, color = colors.textTertiary)
                            Text(
                                text = currencyFormat.format(tipPerPerson),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Bill Amount Input Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = colors.cardBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bill Amount ($)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = billAmount,
                    onValueChange = onBillChanged,
                    placeholder = { Text("0.00", color = colors.textTertiary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.keyEquals,
                        unfocusedBorderColor = colors.keyFunction,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tip_bill_input")
                )
            }
        }

        // Tip Percentage Selection
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = colors.cardBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tip Percentage",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textTertiary
                    )
                    Text(
                        text = "${effectiveTipPercent.toInt()}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.keyEquals
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Standard Tip % Buttons
                val presets = listOf(10, 15, 18, 20, 25)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { pct ->
                        val isSelected = tipPercent == pct && customTipPercent.isEmpty()
                        Surface(
                            onClick = { onTipPercentSelected(pct) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) colors.keyEquals else colors.keyFunction.copy(alpha = 0.6f),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("tip_btn_$pct")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$pct%",
                                    color = if (isSelected) {
                                        if (colors.isDark) Color.Black else Color.White
                                    } else {
                                        colors.textPrimary
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slider for fine tuning
                Slider(
                    value = effectiveTipPercent.toFloat(),
                    onValueChange = { onTipPercentSelected(it.toInt()) },
                    valueRange = 0f..40f,
                    steps = 39,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.keyEquals,
                        activeTrackColor = colors.keyEquals,
                        inactiveTrackColor = colors.keyFunction
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Split with People & Round Up
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = colors.cardBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Split with Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Split Between",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textTertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$peopleCount ${if (peopleCount == 1) "Person" else "People"}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (peopleCount > 1) onPeopleChanged(peopleCount - 1) },
                            enabled = peopleCount > 1,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.keyFunction)
                                .testTag("people_minus_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease people", tint = colors.textPrimary)
                        }

                        IconButton(
                            onClick = { onPeopleChanged(peopleCount + 1) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.keyEquals)
                                .testTag("people_plus_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase people",
                                tint = if (colors.isDark) Color.Black else Color.White
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = colors.keyFunction.copy(alpha = 0.3f)
                )

                // Round Up switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Round Up Total",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Round each person's share to next whole dollar",
                            fontSize = 12.sp,
                            color = colors.textTertiary
                        )
                    }

                    Switch(
                        checked = roundUp,
                        onCheckedChange = onRoundUpToggled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = if (colors.isDark) Color.Black else Color.White,
                            checkedTrackColor = colors.keyEquals,
                            uncheckedThumbColor = colors.textTertiary,
                            uncheckedTrackColor = colors.keyFunction
                        ),
                        modifier = Modifier.testTag("round_up_switch")
                    )
                }
            }
        }
    }
}
