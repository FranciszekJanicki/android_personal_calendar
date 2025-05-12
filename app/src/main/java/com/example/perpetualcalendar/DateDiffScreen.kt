package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDiffScreen(navController: NavController) {
    var startDateInput by remember { mutableStateOf("") }
    var endDateInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Różnica między datami") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = startDateInput,
                onValueChange = { startDateInput = it },
                label = { Text("Data początkowa (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endDateInput,
                onValueChange = { endDateInput = it },
                label = { Text("Data końcowa (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    try {
                        val start = LocalDate.parse(startDateInput, formatter)
                        val end = LocalDate.parse(endDateInput, formatter)
                        val totalDays = ChronoUnit.DAYS.between(start, end).toInt()
                        val workdays = (0..totalDays).map { start.plusDays(it.toLong()) }
                            .count { it.dayOfWeek.value in 1..5 }
                        result = "Liczba dni: $totalDays\nDni robocze: $workdays"
                        error = null
                    } catch (e: Exception) {
                        result = null
                        error = "Niepoprawny format daty. Użyj YYYY-MM-DD."
                    }
                },
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text("Oblicz")
            }

            result?.let {
                Text(it, style = MaterialTheme.typography.bodyLarge)
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
