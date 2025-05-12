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
fun MainScreen(
    onShowEvents: () -> Unit,
    onShowSettings: () -> Unit,
    onShowSundays: () -> Unit,
    onShowWorkdays: () -> Unit,
    onShowDateDiff: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onShowEvents() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Pokaz eventy", style = MaterialTheme.typography.bodyLarge)
            }

            Button(
                onClick = { onShowSettings() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Pokaz ustawienia", style = MaterialTheme.typography.bodyLarge)
            }

            Button(
                onClick = { onShowSundays() },
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
}

// eventy, mozliwosc dodawania informacji
// daty swiat
// notyfikacje push
// integracja z kalendarzem systemowym
// ile dni do konca eventu
// personalny kalendarz z eventami
// lista zakupow
// data poczatkowa i koncowa eventu, cyklicznosc
//