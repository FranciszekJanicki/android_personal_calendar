package com.example.perpetualcalendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.ZoneId

object NotificationScheduler {

    private const val TEN_MINUTES_MILLIS = 10 * 60 * 1000L

    fun scheduleNotification(context: Context, event: Event, beforeStart: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notifyTime = if (beforeStart) {
            event.startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - TEN_MINUTES_MILLIS
        } else {
            event.endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - TEN_MINUTES_MILLIS
        }

        if (notifyTime <= System.currentTimeMillis()) {
            // Too late, dont schedule
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_TITLE, if (beforeStart) "Event zaraz sie rozpocznie" else "Event zaraz sie skonczy")
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "${event.title}\n${event.description}")
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, generateRequestCode(event, beforeStart))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            generateRequestCode(event, beforeStart),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent)
    }

    fun cancelNotification(context: Context, event: Event, beforeStart: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            generateRequestCode(event, beforeStart),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun generateRequestCode(event: Event, beforeStart: Boolean): Int {
        return event.id.hashCode() + if (beforeStart) 1000 else 2000
    }
}
