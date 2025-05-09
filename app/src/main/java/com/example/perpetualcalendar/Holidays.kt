package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun HolidayScreen(
    onShowSundays: (Int, LocalDate) -> Unit,
    onShowWorkdays: () -> Unit
) {
    var year by remember { mutableStateOf(2024) }

    val easter = remember(year) { getEasterDate(year) }
    val popielec = easter.minusDays(46)
    val bozeCialo = easter.plusDays(60)

    var adwent = remember(year) {
        var temp = LocalDate.of(year, 12, 24)
        while (temp.dayOfWeek.value != 7) {
            temp = temp.minusDays(1)
        }
        temp.minusDays(21)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Wybierz rok", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { if (year > 1900) year-- }) {
                Text("-")
            }
            Text(year.toString(), style = MaterialTheme.typography.titleLarge)
            Button(onClick = { if (year < 2200) year++ }) {
                Text("+")
            }
        }

        Divider()

        Text("Popielec: $popielec")
        Text("Wielkanoc: $easter")
        Text("Boże Ciało: $bozeCialo")
        Text("Adwent: $adwent")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onShowSundays(year, easter) }) {
            Text("Pokaż niedziele handlowe")
        }

        Button(onClick = { onShowWorkdays() }) {
            Text("Pokaż dni robocze")
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
