package com.example.perpetualcalendar

import java.time.LocalDate
import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(), // unique ID first
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String = ""  // description last, optional default empty
)
