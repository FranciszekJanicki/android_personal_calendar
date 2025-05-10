package com.example.perpetualcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DateDiffScreen(onBack: () -> Unit) {
    var startYear by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startDay by remember { mutableStateOf("") }

    var endYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endDay by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Oblicz różnicę dni", style = MaterialTheme.typography.headlineMedium)

        Text("Data początkowa", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = startYear,
                onValueChange = { startYear = it },
                label = { Text("Rok") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = startMonth,
                onValueChange = { startMonth = it },
                label = { Text("Miesiąc") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = startDay,
                onValueChange = { startDay = it },
                label = { Text("Dzień") },
                modifier = Modifier.weight(1f)
            )
        }

        Text("Data końcowa", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = endYear,
                onValueChange = { endYear = it },
                label = { Text("Rok") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = endMonth,
                onValueChange = { endMonth = it },
                label = { Text("Miesiąc") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = endDay,
                onValueChange = { endDay = it },
                label = { Text("Dzień") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                try {
                    val start = LocalDate.of(
                        startYear.toInt(), startMonth.toInt(), startDay.toInt()
                    )
                    val end = LocalDate.of(
                        endYear.toInt(), endMonth.toInt(), endDay.toInt()
                    )
                    result = "Różnica: ${kotlin.math.abs(java.time.temporal.ChronoUnit.DAYS.between(start, end))} dni"
                } catch (e: Exception) {
                    result = "Nieprawidłowe dane"
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Oblicz", style = MaterialTheme.typography.bodyLarge)
        }

        result?.let {
            Text(it, style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Powrót")
        }
    }
}
