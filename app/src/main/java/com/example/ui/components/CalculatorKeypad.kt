package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors

enum class KeyType {
    DIGIT,
    OPERATOR,
    FUNCTION,
    EQUALS,
    ACCENT
}

@Composable
fun CalcKey(
    text: String = "",
    icon: ImageVector? = null,
    keyType: KeyType = KeyType.DIGIT,
    modifier: Modifier = Modifier,
    testTag: String = "key_$text",
    onClick: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "keyScale"
    )

    val bgColor = when (keyType) {
        KeyType.DIGIT -> colors.keyDigit
        KeyType.OPERATOR -> colors.keyOperator
        KeyType.FUNCTION -> colors.keyFunction
        KeyType.EQUALS -> colors.keyEquals
        KeyType.ACCENT -> colors.cardBackground
    }

    val contentColor = when (keyType) {
        KeyType.DIGIT -> colors.textPrimary
        KeyType.OPERATOR -> colors.textOperator
        KeyType.FUNCTION -> colors.textFunction
        KeyType.EQUALS -> colors.onKeyEquals
        KeyType.ACCENT -> colors.textOperator
    }

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .padding(3.dp)
            .scale(scale)
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = contentColor.copy(alpha = 0.2f)),
                onClick = {
                    try {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    } catch (_: Exception) {}
                    onClick()
                }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text.ifEmpty { "key icon" },
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                color = contentColor,
                fontSize = if (text.length > 2) 20.sp else 24.sp,
                fontWeight = if (keyType == KeyType.EQUALS || keyType == KeyType.OPERATOR) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEquals: () -> Unit,
    onParenthesis: () -> Unit,
    onTogglePlusMinus: () -> Unit,
    onToggleScientific: () -> Unit,
    isScientificOpen: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(colors.surface)
            .padding(start = 14.dp, end = 14.dp, top = 18.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: AC, ±, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcKey(
                text = "AC",
                keyType = KeyType.FUNCTION,
                modifier = Modifier.weight(1f),
                testTag = "key_clear",
                onClick = onClear
            )
            CalcKey(
                text = "±",
                keyType = KeyType.FUNCTION,
                modifier = Modifier.weight(1f),
                testTag = "key_plus_minus",
                onClick = onTogglePlusMinus
            )
            CalcKey(
                text = "%",
                keyType = KeyType.FUNCTION,
                modifier = Modifier.weight(1f),
                testTag = "key_percent",
                onClick = { onOperator("%") }
            )
            CalcKey(
                text = "÷",
                keyType = KeyType.OPERATOR,
                modifier = Modifier.weight(1f),
                testTag = "key_divide",
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcKey(
                text = "7",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_7",
                onClick = { onDigit("7") }
            )
            CalcKey(
                text = "8",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_8",
                onClick = { onDigit("8") }
            )
            CalcKey(
                text = "9",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_9",
                onClick = { onDigit("9") }
            )
            CalcKey(
                text = "×",
                keyType = KeyType.OPERATOR,
                modifier = Modifier.weight(1f),
                testTag = "key_multiply",
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcKey(
                text = "4",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_4",
                onClick = { onDigit("4") }
            )
            CalcKey(
                text = "5",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_5",
                onClick = { onDigit("5") }
            )
            CalcKey(
                text = "6",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_6",
                onClick = { onDigit("6") }
            )
            CalcKey(
                text = "−",
                keyType = KeyType.OPERATOR,
                modifier = Modifier.weight(1f),
                testTag = "key_subtract",
                onClick = { onOperator("−") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcKey(
                text = "1",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_1",
                onClick = { onDigit("1") }
            )
            CalcKey(
                text = "2",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_2",
                onClick = { onDigit("2") }
            )
            CalcKey(
                text = "3",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_3",
                onClick = { onDigit("3") }
            )
            CalcKey(
                text = "+",
                keyType = KeyType.OPERATOR,
                modifier = Modifier.weight(1f),
                testTag = "key_add",
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ( ), 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcKey(
                text = "( )",
                keyType = KeyType.FUNCTION,
                modifier = Modifier.weight(1f),
                testTag = "key_parenthesis",
                onClick = onParenthesis
            )
            CalcKey(
                text = "0",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_0",
                onClick = { onDigit("0") }
            )
            CalcKey(
                text = ".",
                keyType = KeyType.DIGIT,
                modifier = Modifier.weight(1f),
                testTag = "key_decimal",
                onClick = onDecimal
            )
            CalcKey(
                text = "=",
                keyType = KeyType.EQUALS,
                modifier = Modifier.weight(1f),
                testTag = "key_equals",
                onClick = onEquals
            )
        }

        // Bottom Home Indicator Handle
        Box(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 2.dp)
                .width(64.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.handleColor)
        )
    }
}
