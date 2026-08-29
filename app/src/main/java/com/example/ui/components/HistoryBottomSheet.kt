package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalculationEntity
import com.example.ui.theme.LocalCalculatorColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    historyList: List<CalculationEntity>,
    favoriteList: List<CalculationEntity>,
    onSelectCalculation: (CalculationEntity, Boolean) -> Unit,
    onToggleFavorite: (CalculationEntity) -> Unit,
    onUpdateNote: (CalculationEntity, String) -> Unit,
    onDeleteItem: (CalculationEntity) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = All, 1 = Starred
    var searchQuery by remember { mutableStateOf("") }
    var editingEntityForNote by remember { mutableStateOf<CalculationEntity?>(null) }
    var noteInputText by remember { mutableStateOf("") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val activeList = if (selectedTab == 0) historyList else favoriteList
    val filteredList = remember(activeList, searchQuery) {
        if (searchQuery.isBlank()) activeList
        else activeList.filter {
            it.expression.contains(searchQuery, ignoreCase = true) ||
            it.result.contains(searchQuery, ignoreCase = true) ||
            it.note.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        contentColor = colors.textPrimary,
        modifier = modifier.testTag("history_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculation History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                if (historyList.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("clear_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear all history",
                            tint = colors.textTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs: All vs Starred
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = colors.cardBackground,
                contentColor = colors.keyEquals,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All (${historyList.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Starred (${favoriteList.size})", fontWeight = FontWeight.SemiBold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search calculations or notes...", color = colors.textTertiary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colors.textTertiary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = colors.textTertiary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.keyEquals,
                    unfocusedBorderColor = colors.cardBackground,
                    focusedContainerColor = colors.cardBackground,
                    unfocusedContainerColor = colors.cardBackground,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // List or Empty state
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Outlined.StarBorder else Icons.Default.History,
                            contentDescription = null,
                            tint = colors.textTertiary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No calculations match '$searchQuery'"
                            else if (selectedTab == 1) "No starred calculations yet"
                            else "No calculations saved yet",
                            color = colors.textSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Calculations are saved automatically when you press =",
                            color = colors.textTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            dateStr = dateFormat.format(Date(item.timestamp)),
                            onSelectExpression = { onSelectCalculation(item, false) },
                            onSelectResult = { onSelectCalculation(item, true) },
                            onToggleFavorite = { onToggleFavorite(item) },
                            onOpenNoteDialog = {
                                editingEntityForNote = item
                                noteInputText = item.note
                            },
                            onDelete = { onDeleteItem(item) },
                            onCopy = { text ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Calculation", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog for adding / editing note on calculation
    if (editingEntityForNote != null) {
        AlertDialog(
            onDismissRequest = { editingEntityForNote = null },
            title = { Text("Add Label / Note") },
            text = {
                OutlinedTextField(
                    value = noteInputText,
                    onValueChange = { noteInputText = it },
                    placeholder = { Text("e.g. Rent, Grocery, Flight ticket") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editingEntityForNote?.let { entity ->
                            onUpdateNote(entity, noteInputText.trim())
                        }
                        editingEntityForNote = null
                    }
                ) {
                    Text("Save", color = colors.keyEquals)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEntityForNote = null }) {
                    Text("Cancel")
                }
            },
            containerColor = colors.surface,
            textContentColor = colors.textPrimary,
            titleContentColor = colors.textPrimary
        )
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All History") },
            text = { Text("Are you sure you want to delete all saved calculations? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Clear All", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = colors.surface,
            textContentColor = colors.textPrimary,
            titleContentColor = colors.textPrimary
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: CalculationEntity,
    dateStr: String,
    onSelectExpression: () -> Unit,
    onSelectResult: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenNoteDialog: () -> Unit,
    onDelete: () -> Unit,
    onCopy: (String) -> Unit
) {
    val colors = LocalCalculatorColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.cardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Category Badge + Timestamp + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = colors.keyFunction.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = item.category,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = dateStr,
                        color = colors.textTertiary,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Star/Favorite button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star calculation",
                            tint = if (item.isFavorite) colors.keyEquals else colors.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Add / edit note button
                    IconButton(
                        onClick = onOpenNoteDialog,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (item.note.isNotEmpty()) Icons.Default.EditNote else Icons.Default.NoteAdd,
                            contentDescription = "Add note",
                            tint = if (item.note.isNotEmpty()) colors.keyEquals else colors.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete item",
                            tint = colors.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Note tag if attached
            if (item.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colors.keyEquals.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "📝 ${item.note}",
                        color = colors.keyEquals,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expression Row (tap to insert into calc)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectExpression() }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.expression,
                    color = colors.textSecondary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )

                IconButton(
                    onClick = { onCopy(item.expression) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy expression",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Result Row (tap to insert result only)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectResult() }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "= ${item.result}",
                    color = colors.keyEquals,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { onCopy(item.result) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy result",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
