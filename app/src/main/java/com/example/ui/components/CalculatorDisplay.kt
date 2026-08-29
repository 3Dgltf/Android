package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AngleMode
import com.example.ui.theme.LocalCalculatorColors

@Composable
fun CalculatorDisplay(
    expression: String,
    liveResult: String,
    isEvaluated: Boolean,
    angleMode: AngleMode,
    isScientificActive: Boolean,
    onToggleAngleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val exprScrollState = rememberScrollState()

    // Auto-scroll to end when expression changes
    LaunchedEffect(expression) {
        if (expression.isNotEmpty()) {
            exprScrollState.animateScrollTo(exprScrollState.maxValue)
        }
    }

    val displayExpr = if (expression.isEmpty()) "0" else expression
    val displayExprSize = when {
        displayExpr.length > 20 -> 24.sp
        displayExpr.length > 14 -> 30.sp
        displayExpr.length > 8 -> 38.sp
        else -> 46.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Top status badges row (Angle Mode & Parenthesis count)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isScientificActive) {
                    Surface(
                        onClick = onToggleAngleMode,
                        shape = RoundedCornerShape(8.dp),
                        color = colors.cardBackground,
                        modifier = Modifier.testTag("angle_mode_badge")
                    ) {
                        Text(
                            text = if (angleMode == AngleMode.DEGREE) "DEG" else "RAD",
                            color = colors.textOperator,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Show open parenthesis indicator if any
                val openCount = expression.count { it == '(' } - expression.count { it == ')' }
                if (openCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.keyFunction.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "($openCount",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Copy to clipboard action
            if (expression.isNotEmpty() || liveResult.isNotEmpty()) {
                IconButton(
                    onClick = {
                        val textToCopy = if (isEvaluated && liveResult.isNotEmpty()) liveResult else if (liveResult.isNotEmpty()) liveResult else expression
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Calculator Result", textToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("copy_result_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy result",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Expression Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(exprScrollState),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayExpr,
                color = if (isEvaluated) colors.textSecondary else colors.textPrimary,
                fontSize = if (isEvaluated) 28.sp else displayExprSize,
                fontWeight = if (isEvaluated) FontWeight.Normal else FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("calculator_expression")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Live preview or Evaluated Result Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            this@Column.AnimatedVisibility(
                visible = liveResult.isNotEmpty(),
                enter = fadeIn() + scaleIn(initialScale = 0.95f),
                exit = fadeOut() + scaleOut(targetScale = 0.95f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!isEvaluated && liveResult.isNotEmpty()) {
                        Text(
                            text = "= ",
                            color = colors.textTertiary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Light
                        )
                    }

                    Text(
                        text = liveResult,
                        color = if (isEvaluated) colors.keyEquals else colors.textSecondary,
                        fontSize = if (isEvaluated) 44.sp else 30.sp,
                        fontWeight = if (isEvaluated) FontWeight.Bold else FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("calculator_result")
                    )
                }
            }
        }
    }
}
