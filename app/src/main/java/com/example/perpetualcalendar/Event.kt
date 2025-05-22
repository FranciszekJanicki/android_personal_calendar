package com.example.perpetualcalendar

import java.util.UUID
import java.time.LocalDateTime

enum class Recurrence {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY
}

fun recurrenceToDisplayName(recurrence: Recurrence?): String = when (recurrence) {
    Recurrence.NONE -> "Brak"
    Recurrence.DAILY -> "Codziennie"
    Recurrence.WEEKLY -> "Co tydzień"
    Recurrence.MONTHLY -> "Co miesiąc"
    else -> "Brak"
}


data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val description: String = "",
    val recurrence :Recurrence = Recurrence.NONE
)
