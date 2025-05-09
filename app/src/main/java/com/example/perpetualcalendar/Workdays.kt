package com.example.perpetualcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.floor

class Workdays : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkdaysScreen(onBack = { finish() })
        }
    }
}

@Composable
fun WorkdaysScreen(onBack: () -> Unit) {
    var startDate by remember { mutableStateOf(LocalDate.of(2023, 1, 1)) }
    var endDate by remember { mutableStateOf(LocalDate.of(2023, 1, 2)) }

    val result = calculateWorkdays(startDate, endDate)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dni robocze") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            DatePicker("Data początkowa", startDate) { startDate = it }
            Spacer(Modifier.height(8.dp))
            DatePicker("Data końcowa", endDate) { endDate = it }
            Spacer(Modifier.height(16.dp))

            Text(
                text = when {
                    startDate.isAfter(endDate) -> "Błędny zakres"
                    ChronoUnit.YEARS.between(startDate, endDate) > 2 -> "Zbyt duży okres"
                    else -> "Dni: ${result.first}\nDni roboczych: ${result.second}"
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun DatePicker(label: String, date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    Column {
        Text(label)
        DatePickerDialog(date = date, onDateSelected = onDateChange)
    }
}

// Replace this with a Compose-compatible date picker or implement a dialog-based one.
// Here’s a placeholder (doesn't work unless you implement your own or use a lib)
@Composable
fun DatePickerDialog(date: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    // For production: use a library like MaterialDialogs or your own dialog implementation.
    // Here we only show the current date (you must wire a real date picker).
    Text("📅 ${date.toString()} (picker not implemented)")
}

fun calculateWorkdays(startDate: LocalDate, endDate: LocalDate): Pair<Int, Int> {
    var date = startDate
    val easterDates = mutableMapOf<Int, LocalDate>()
    var totalDays = 0
    var workdays = 0

    while (!date.isAfter(endDate)) {
        totalDays++
        if (!isHoliday(date, easterDates)) {
            workdays++
        }
        date = date.plusDays(1)
    }

    return Pair(totalDays, workdays)
}

fun isHoliday(date: LocalDate, easterDates: MutableMap<Int, LocalDate>): Boolean {
    // Weekends
    if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) return true

    // Fixed Polish holidays
    val holidays = listOf(
        LocalDate.of(date.year, 1, 1),
        LocalDate.of(date.year, 1, 6),
        LocalDate.of(date.year, 5, 1),
        LocalDate.of(date.year, 5, 3),
        LocalDate.of(date.year, 8, 15),
        LocalDate.of(date.year, 11, 1),
        LocalDate.of(date.year, 11, 11),
        LocalDate.of(date.year, 12, 25),
        LocalDate.of(date.year, 12, 26)
    )

    if (date in holidays) return true

    // Easter Monday
    val easter = easterDates.getOrPut(date.year) { getEasterDate(date.year) }
    if (date == easter.plusDays(1)) return true

    return false
}

fun getEasterDate(year: Int): LocalDate {
    val a = year % 19
    val b = floor(year / 100.0).toInt()
    val c = year % 100
    val d = floor(b / 4.0).toInt()
    val e = b % 4
    val f = floor((b + 8) / 25.0).toInt()
    val g = floor((b - f + 1) / 3.0).toInt()
    val h = (19 * a + b - d - g + 15) % 30
    val i = floor(c / 4.0).toInt()
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = floor((a + 11 * h + 22 * l) / 451.0).toInt()
    val p = (h + l - 7 * m + 114) % 31
    val day = p + 1
    val month = floor((h + l - 7 * m + 114) / 31.0).toInt()
    return LocalDate.of(year, month, day)
}
