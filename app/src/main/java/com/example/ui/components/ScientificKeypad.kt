package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.engine.AngleMode
import com.example.ui.theme.LocalCalculatorColors

@Composable
fun ScientificKeypad(
    isOpen: Boolean,
    isInverse: Boolean,
    angleMode: AngleMode,
    onToggleInverse: () -> Unit,
    onToggleAngleMode: () -> Unit,
    onFunction: (String) -> Unit,
    onConstant: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface.copy(alpha = 0.6f))
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Row 1: 2nd, RAD/DEG, sin/asin, cos/acos, tan/atan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalcKey(
                    text = "2nd",
                    keyType = if (isInverse) KeyType.ACCENT else KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_2nd",
                    onClick = onToggleInverse
                )
                CalcKey(
                    text = if (angleMode == AngleMode.DEGREE) "DEG" else "RAD",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_angle_toggle",
                    onClick = onToggleAngleMode
                )
                CalcKey(
                    text = if (isInverse) "sin⁻¹" else "sin",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_sin",
                    onClick = { onFunction(if (isInverse) "asin" else "sin") }
                )
                CalcKey(
                    text = if (isInverse) "cos⁻¹" else "cos",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_cos",
                    onClick = { onFunction(if (isInverse) "acos" else "cos") }
                )
                CalcKey(
                    text = if (isInverse) "tan⁻¹" else "tan",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_tan",
                    onClick = { onFunction(if (isInverse) "atan" else "tan") }
                )
            }

            // Row 2: ln/e^x, log/10^x, x^y, √/x², x!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalcKey(
                    text = if (isInverse) "eˣ" else "ln",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_ln",
                    onClick = { onFunction(if (isInverse) "e^x" else "ln") }
                )
                CalcKey(
                    text = if (isInverse) "10ˣ" else "log",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_log",
                    onClick = { onFunction(if (isInverse) "10^x" else "log") }
                )
                CalcKey(
                    text = "xʸ",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_power",
                    onClick = { onFunction("x^y") }
                )
                CalcKey(
                    text = if (isInverse) "x²" else "√",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_sqrt",
                    onClick = { onFunction(if (isInverse) "x²" else "sqrt") }
                )
                CalcKey(
                    text = "x!",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_factorial",
                    onClick = { onFunction("x!") }
                )
            }

            // Row 3: 1/x, |x|, π, e, ⌫
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalcKey(
                    text = "1/x",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_inv",
                    onClick = { onFunction("1/x") }
                )
                CalcKey(
                    text = "|x|",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_abs",
                    onClick = { onFunction("abs") }
                )
                CalcKey(
                    text = "π",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_pi",
                    onClick = { onConstant("π") }
                )
                CalcKey(
                    text = "e",
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_e",
                    onClick = { onConstant("e") }
                )
                CalcKey(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    keyType = KeyType.FUNCTION,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp),
                    testTag = "key_backspace",
                    onClick = onDelete
                )
            }
        }
    }
}
