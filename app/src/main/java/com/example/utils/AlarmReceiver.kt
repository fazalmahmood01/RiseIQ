package com.example.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarm_id", -1)
        val label = intent.getStringExtra("alarm_label") ?: "Smart Alarm"
        val hour = intent.getIntExtra("alarm_hour", 8)
        val minute = intent.getIntExtra("alarm_minute", 0)

        Log.d("AlarmReceiver", "Alarm onReceive triggered! ID: $alarmId - $label")

        // Trigger notification
        NotificationHelper.showAlarmNotification(context, alarmId, label, "$hour:${String.format("%02d", minute)}")

        // Retrieve full alarm details and trigger the in-app active ringing overlay
        val db = AppDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = if (alarmId != -1) {
                db.alarmDao().getAlarmById(alarmId)
            } else null

            val finalAlarm = alarm ?: Alarm(
                id = alarmId,
                hour = hour,
                minute = minute,
                label = label,
                missionType = "MATH" // default mission if database cannot be read
            )

            // Update state
            CoroutineScope(Dispatchers.Main).launch {
                ActiveAlarmManager.triggerAlarm(finalAlarm)
            }
        }
    }
}
