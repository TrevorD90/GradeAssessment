package com.gradeassessment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OrderingQuestion(
    items: List<String>,
    onOrderChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Arrange in the correct order:",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(8.dp))

        items.forEachIndexed { index, item ->
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(30.dp)
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Column {
                        IconButton(
                            onClick = {
                                if (index > 0) {
                                    val newList = items.toMutableList()
                                    newList[index] = items[index - 1]
                                    newList[index - 1] = items[index]
                                    onOrderChanged(newList)
                                }
                            },
                            enabled = index > 0
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, "Move $item up")
                        }
                        IconButton(
                            onClick = {
                                if (index < items.lastIndex) {
                                    val newList = items.toMutableList()
                                    newList[index] = items[index + 1]
                                    newList[index + 1] = items[index]
                                    onOrderChanged(newList)
                                }
                            },
                            enabled = index < items.lastIndex
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, "Move $item down")
                        }
                    }
                }
            }
        }
    }
}
