package com.example.perpetualcalendar

import java.util.UUID
import java.time.LocalDateTime

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val description: String = ""
)
