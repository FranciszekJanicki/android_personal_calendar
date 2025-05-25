package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDiffScreen(navController: NavController) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            ?: Instant.now().toEpochMilli()
    )

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            ?: Instant.now().toEpochMilli()
    )

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
            TextButton(onClick = { showStartPicker = true }) {
                Text(startDate?.format(formatter) ?: "Wybierz datę początkową")
            }

            TextButton(onClick = { showEndPicker = true }) {
                Text(endDate?.format(formatter) ?: "Wybierz datę końcową")
            }

            Button(
                onClick = {
                    if (startDate == null || endDate == null) {
                        error = "Obie daty muszą być wybrane."
                        result = null
                        return@Button
                    }

                    if (endDate!!.isBefore(startDate)) {
                        error = "Data końcowa nie może być wcześniejsza niż początkowa."
                        result = null
                        return@Button
                    }

                    val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt()
                    val workdays = (0..totalDays).map { startDate!!.plusDays(it.toLong()) }
                        .count { it.dayOfWeek.value in 1..5 }

                    result = "Liczba dni: $totalDays\nDni robocze: $workdays"
                    error = null
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

        if (showStartPicker) {
            DatePickerDialog(
                onDismissRequest = { showStartPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val millis = startDatePickerState.selectedDateMillis
                        if (millis != null) {
                            startDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showStartPicker = false
                    }) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(state = startDatePickerState)
            }
        }

        if (showEndPicker) {
            DatePickerDialog(
                onDismissRequest = { showEndPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val millis = endDatePickerState.selectedDateMillis
                        if (millis != null) {
                            endDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showEndPicker = false
                    }) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(state = endDatePickerState)
            }
        }
    }
}
