package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun DiscountCalculatorView(
    originalPrice: String,
    discountPercent: String,
    taxPercent: String,
    onPriceChanged: (String) -> Unit,
    onDiscountChanged: (String) -> Unit,
    onTaxChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val price = originalPrice.toDoubleOrNull() ?: 0.0
    val discount = discountPercent.toDoubleOrNull() ?: 0.0
    val tax = taxPercent.toDoubleOrNull() ?: 0.0

    val savingsAmount = price * (discount / 100.0)
    val discountedPrice = price - savingsAmount
    val taxAmount = discountedPrice * (tax / 100.0)
    val finalPrice = discountedPrice + taxAmount

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
        // Hero Card: Final Price & Savings
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
                        text = "FINAL PAYABLE PRICE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currencyFormat.format(finalPrice),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.keyEquals,
                        modifier = Modifier.testTag("discount_final_price")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "You Save", fontSize = 12.sp, color = colors.textTertiary)
                            Text(
                                text = currencyFormat.format(savingsAmount),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981) // Green for savings
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(colors.keyFunction.copy(alpha = 0.4f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Sales Tax", fontSize = 12.sp, color = colors.textTertiary)
                            Text(
                                text = currencyFormat.format(taxAmount),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Original Price Input
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = colors.cardBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Original Price ($)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = originalPrice,
                    onValueChange = onPriceChanged,
                    placeholder = { Text("100.00", color = colors.textTertiary) },
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
                        .testTag("discount_price_input")
                )
            }
        }

        // Discount % Selection
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
                        text = "Discount Percentage",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textTertiary
                    )
                    Text(
                        text = "${discount.toInt()}% OFF",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.keyEquals
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Discount Quick Chips
                val discountChips = listOf(10, 15, 20, 25, 30, 50, 70)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    discountChips.take(4).forEach { pct ->
                        val isSelected = discount == pct.toDouble()
                        Surface(
                            onClick = { onDiscountChanged(pct.toString()) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) colors.keyEquals else colors.keyFunction.copy(alpha = 0.6f),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
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
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    discountChips.drop(4).forEach { pct ->
                        val isSelected = discount == pct.toDouble()
                        Surface(
                            onClick = { onDiscountChanged(pct.toString()) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) colors.keyEquals else colors.keyFunction.copy(alpha = 0.6f),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
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
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = discount.coerceIn(0.0, 100.0).toFloat(),
                    onValueChange = { onDiscountChanged(it.toInt().toString()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.keyEquals,
                        activeTrackColor = colors.keyEquals,
                        inactiveTrackColor = colors.keyFunction
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Tax % Section
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = colors.cardBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sales Tax (%)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = taxPercent,
                    onValueChange = onTaxChanged,
                    placeholder = { Text("0%", color = colors.textTertiary) },
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
                        .testTag("discount_tax_input")
                )
            }
        }
    }
}
