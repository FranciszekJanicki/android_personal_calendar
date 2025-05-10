package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WorkdaysScreen(
    onBack: () -> Unit
) {
    val workdays = listOf(
        "Poniedziałek", // Monday
        "Wtorek",       // Tuesday
        "Środa",        // Wednesday
        "Czwartek",     // Thursday
        "Piątek"        // Friday
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dni robocze",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(workdays) { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(onClick = { onBack() }) {
            Text("Powrót")
        }
    }
}
