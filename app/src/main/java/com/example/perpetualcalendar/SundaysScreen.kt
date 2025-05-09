package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun SundaysScreen(
    year: Int,
    easter: LocalDate?,
    onBack: () -> Unit
) {
    val sundayDates = getCommercialSundays(year, easter)

    val headerText = when {
        year <= 0 -> "Błąd danych wejściowych - rok"
        year < 2020 -> "Niedziele handlowe w $year roku nie mają sensu"
        else -> "Niedziele handlowe w $year roku"
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = headerText, style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sundayDates.sorted()) { date ->
                Text(text = date.toString(), style = MaterialTheme.typography.bodyLarge)
            }
        }

        Button(onClick = { onBack() }) {
            Text("Powrót")
        }
    }
}

fun getCommercialSundays(year: Int, easter: LocalDate?): List<LocalDate> {
    val result = mutableListOf<LocalDate>()

    // Last Sundays of Jan, Apr, Jun, Aug
    val months = listOf(1, 4, 6, 8)
    for (month in months) {
        val firstOfNextMonth = LocalDate.of(year, month + 1, 1)
        val lastSunday = findLastSundayBefore(firstOfNextMonth)
        result.add(lastSunday)
    }

    // Easter Sunday
    if (easter != null) {
        result.add(findLastSundayBefore(easter.plusDays(1)))
    }

    // Two Sundays before and on Christmas Eve
    result.add(findLastSundayBefore(LocalDate.of(year, 12, 17)))
    result.add(findLastSundayBefore(LocalDate.of(year, 12, 24)))

    return result
}

fun findLastSundayBefore(date: LocalDate): LocalDate {
    var current = date.minusDays(1)
    while (current.dayOfWeek.value != 7) {
        current = current.minusDays(1)
    }
    return current
}
