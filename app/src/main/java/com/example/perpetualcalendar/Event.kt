package com.example.perpetualcalendar

import java.time.LocalDate

data class Event(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class EventDetails(
    val name: String,
    val date: LocalDate,
    var description: String
)
