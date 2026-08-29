package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.getCustomColorsForPreset

@Composable
fun ThemeSelectorDialog(
    isOpen: Boolean,
    currentPreset: AppThemePreset,
    onSelectPreset: (AppThemePreset) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val colors = LocalCalculatorColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Aesthetic Theme",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppThemePreset.values().forEach { preset ->
                    val isSelected = preset == currentPreset
                    val presetColors = getCustomColorsForPreset(preset)

                    Surface(
                        onClick = { onSelectPreset(preset) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) colors.cardBackground else colors.surface.copy(alpha = 0.5f),
                        border = if (isSelected) {
                            androidx.compose.foundation.BorderStroke(1.5.dp, colors.keyEquals)
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("theme_option_${preset.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Swatch indicator
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(presetColors.background)
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(presetColors.keyEquals)
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                }

                                Text(
                                    text = preset.displayName,
                                    color = colors.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = colors.keyEquals,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = colors.keyEquals, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = colors.surface,
        textContentColor = colors.textPrimary,
        titleContentColor = colors.textPrimary,
        shape = RoundedCornerShape(24.dp)
    )
}
