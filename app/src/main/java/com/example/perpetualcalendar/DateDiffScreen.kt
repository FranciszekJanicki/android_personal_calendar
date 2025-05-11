package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun DateDiffScreen(onBack: () -> Unit) {
    var startYear by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startDay by remember { mutableStateOf("") }

    var endYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endDay by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Oblicz różnicę dni", style = MaterialTheme.typography.headlineMedium)

        Text("Data początkowa", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = startYear,
                onValueChange = { startYear = it },
                label = { Text("Rok") },
                modifier = Modifier.weight(1f),
                isError = errorMessage != null
            )
            TextField(
                value = startMonth,
                onValueChange = { startMonth = it },
                label = { Text("Miesiąc") },
                modifier = Modifier.weight(1f),
                isError = errorMessage != null
            )
            TextField(
                value = startDay,
                onValueChange = { startDay = it },
                label = { Text("Dzień") },
                modifier = Modifier.weight(1f),
                isError = errorMessage != null
            )
        }

        Text("Data końcowa", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = endYear,
                onValueChange = { endYear = it },
                label = { Text("Rok") },
                modifier = Modifier.weight(1f),
                isError = errorMessage != null
            )
            TextField(
                value = endMonth,
                onValueChange = { endMonth = it },
                label = { Text("Miesiąc") },
                modifier = Modifier.weight(1f),
                isError = errorMessage != null
            )
            TextField(
                value = endDay,
                onValueChange = { endDay = it },
                label = { Text("Dzień") },
                modifier = Modifier.weight(1f),
                isError = errorMessage != null
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                errorMessage = null

                try {
                    val startYearInt = startYear.toInt()
                    val startMonthInt = startMonth.toInt()
                    val startDayInt = startDay.toInt()
                    val endYearInt = endYear.toInt()
                    val endMonthInt = endMonth.toInt()
                    val endDayInt = endDay.toInt()

                    // Validate inputs
                    if (startYearInt < 1900 || startYearInt > 2200 || endYearInt < 1900 || endYearInt > 2200) {
                        throw IllegalArgumentException("Rok musi być pomiędzy 1900 a 2200")
                    }
                    if (startMonthInt < 1 || startMonthInt > 12 || endMonthInt < 1 || endMonthInt > 12) {
                        throw IllegalArgumentException("Miesiąc musi być pomiędzy 1 a 12")
                    }
                    if (startDayInt < 1 || startDayInt > 31 || endDayInt < 1 || endDayInt > 31) {
                        throw IllegalArgumentException("Dzień musi być pomiędzy 1 a 31")
                    }

                    val start = LocalDate.of(startYearInt, startMonthInt, startDayInt)
                    val end = LocalDate.of(endYearInt, endMonthInt, endDayInt)

                    val from = if (start <= end) start else end
                    val to = if (start <= end) end else start

                    val holidays = mutableSetOf<LocalDate>()
                    for (year in from.year..to.year) {
                        holidays += getPolishPublicHolidays(year)
                    }

                    var workdays = 0
                    var current = from
                    while (!current.isAfter(to)) {
                        val dow = current.dayOfWeek.value
                        if (dow in 1..5 && current !in holidays) {
                            workdays++
                        }
                        current = current.plusDays(1)
                    }

                    val daysBetween = ChronoUnit.DAYS.between(start, end).let { kotlin.math.abs(it) }

                    result = "Różnica: $daysBetween dni\nDni robocze (bez świąt): $workdays"

                } catch (e: NumberFormatException) {
                    errorMessage = "Proszę podać poprawne liczby"
                } catch (e: IllegalArgumentException) {
                    errorMessage = e.message
                } catch (e: Exception) {
                    errorMessage = "Nieprawidłowe dane"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Oblicz", style = MaterialTheme.typography.titleMedium)
        }

        result?.let {
            Text(it, style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Powrót", style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun getPolishPublicHolidays(year: Int): Set<LocalDate> {
    val fixed = setOf(
        LocalDate.of(year, 1, 1),
        LocalDate.of(year, 1, 6),
        LocalDate.of(year, 5, 1),
        LocalDate.of(year, 5, 3),
        LocalDate.of(year, 8, 15),
        LocalDate.of(year, 11, 1),
        LocalDate.of(year, 11, 11),
        LocalDate.of(year, 12, 25),
        LocalDate.of(year, 12, 26)
    )

    val easter = getEasterDate(year)
    val movable = setOf(
        easter,
        easter.plusDays(1),
        easter.plusDays(60)
    )

    return fixed + movable
}
