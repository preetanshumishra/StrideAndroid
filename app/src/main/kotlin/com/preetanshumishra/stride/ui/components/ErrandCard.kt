package com.preetanshumishra.stride.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preetanshumishra.stride.data.models.Errand

@Composable
fun ErrandCard(
    errand: Errand,
    onEdit: () -> Unit = {},
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = errand.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                errand.category?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Priority: ${errand.priority}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                errand.deadline?.let { deadlineStr ->
                    val displayDate = try {
                        val instant = java.time.Instant.parse(deadlineStr)
                        val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")
                        val isOverdue = localDate.isBefore(java.time.LocalDate.now()) && errand.status != "done"
                        Pair(localDate.format(formatter), isOverdue)
                    } catch (e: Exception) { null }

                    displayDate?.let { (dateStr, isOverdue) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row {
                if (errand.status != "done") {
                    IconButton(
                        onClick = onComplete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Complete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
