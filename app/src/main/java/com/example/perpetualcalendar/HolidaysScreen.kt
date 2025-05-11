package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun HolidayScreen(
    onShowSundays: (Int, LocalDate) -> Unit,
    onShowWorkdays: () -> Unit,
    onShowDateDiff: () -> Unit
) {
    var year by remember { mutableStateOf(2024) }
    var yearText by remember { mutableStateOf(year.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val easter = remember(year) { getEasterDate(year) }
    val popielec = easter.minusDays(46)
    val bozeCialo = easter.plusDays(60)

    val adwent = remember(year) {
        var temp = LocalDate.of(year, 12, 24)
        while (temp.dayOfWeek.value != 7) {
            temp = temp.minusDays(1)
        }
        temp.minusDays(21)
    }

    val holidays = listOf(
        "Popielec" to popielec,
        "Wielkanoc" to easter,
        "Boże Ciało" to bozeCialo,
        "Adwent" to adwent
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Wybierz rok", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = yearText,
            onValueChange = {
                yearText = it
                val parsedYear = it.toIntOrNull()

                if (parsedYear != null && parsedYear in 1900..2200) {
                    year = parsedYear
                    errorMessage = null
                } else {
                    errorMessage = "Rok musi być pomiędzy 1900 a 2200"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Rok") },
            textStyle = TextStyle(fontSize = 24.sp)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Divider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(holidays) { (holidayName, holidayDate) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = holidayName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = holidayDate.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onShowSundays(year, easter) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Pokaż niedziele handlowe", style = MaterialTheme.typography.bodyLarge)
        }

        Button(
            onClick = { onShowWorkdays() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Pokaż dni robocze", style = MaterialTheme.typography.bodyLarge)
        }

        Button(
            onClick = { onShowDateDiff() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Oblicz różnicę dni", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun getEasterDate(year: Int): LocalDate {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val month = (h + l - 7 * m + 114) / 31
    val day = ((h + l - 7 * m + 114) % 31) + 1
    return LocalDate.of(year, month, day)
}
