package com.example.perpetualcalendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.ZoneId

fun scheduleNotification(context: Context, event: Event, atStart: Boolean) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra(NotificationReceiver.EXTRA_TITLE, if (atStart) "Start wydarzenia" else "Koniec wydarzenia")
        putExtra(NotificationReceiver.EXTRA_MESSAGE, "${event.title}\n${event.description}")
        putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, generateRequestCode(event, atStart))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        generateRequestCode(event, atStart),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val timeInMillis = if (atStart) {
        event.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } else {
        event.endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
}

fun cancelNotification(context: Context, event: Event, atStart: Boolean) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        generateRequestCode(event, atStart),
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}

private fun generateRequestCode(event: Event, atStart: Boolean): Int {
    return event.id.hashCode() + if (atStart) 1 else 2
}
