package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun StudentProfileScreen(
    onSubmit: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Student Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(32.dp))

        Card(modifier = Modifier.widthIn(max = 400.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        ageText = it.filter { c -> c.isDigit() }
                        ageError = null
                    },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = ageError != null,
                    supportingText = ageError?.let { { Text(it) } }
                )

                Button(
                    onClick = {
                        val age = ageText.toIntOrNull()
                        when {
                            firstName.isBlank() -> ageError = "Please enter a name"
                            age == null || age < 4 || age > 18 -> ageError = "Age must be between 4 and 18"
                            else -> onSubmit(firstName.trim(), age)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = firstName.isNotBlank() && ageText.isNotBlank()
                ) {
                    Text("Continue")
                }
            }
        }
    }
}
