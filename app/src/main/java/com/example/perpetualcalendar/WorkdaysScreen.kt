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
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title Text
        Text(
            text = "Dni robocze",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Lazy Column for Workdays
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(workdays) { day ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Spacer to add some space between list and button
        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        Button(
            onClick = { onBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Powrót", style = MaterialTheme.typography.titleMedium)
        }
    }
}
