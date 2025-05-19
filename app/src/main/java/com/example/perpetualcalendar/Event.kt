package com.example.perpetualcalendar

import java.util.UUID
import java.time.LocalDateTime

enum class Recurrence {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY
}

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val description: String = "",
    val recurrence :Recurrence = Recurrence.NONE
)
