package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WorkdaysScreen(
    onBack: () -> Unit
) {
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

        Text(
            text = "Tutaj możesz dodać logikę wyświetlania dni roboczych.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { onBack() }) {
            Text("Powrót")
        }
    }
}
