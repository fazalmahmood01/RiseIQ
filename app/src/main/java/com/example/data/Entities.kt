package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean = true,
    val recurringDays: String = "", // Comma-separated: "Mon,Wed,Fri" or empty for once
    val missionType: String = "NONE", // NONE, MATH, SHAKE, QR, WALK, SELFIE
    val missionDifficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val isLoudMode: Boolean = false,
    val increasingVolume: Boolean = true,
    val vibrate: Boolean = true,
    val flashlight: Boolean = false,
    val ringtoneName: String = "Radiant Pulse" // Built-in ringtone names
)

@Entity(tableName = "reminders")
data class TaskReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val timestamp: Long, // Date and time in millis
    val repeatType: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY
    val isEnabled: Boolean = true,
    val isVoiceReminder: Boolean = false,
    val voiceText: String = "",
    val voiceLanguage: String = "ENG", // ENG, HIN, URD
    val voiceClipPath: String? = null // For recorded custom user voice message
)

@Entity(tableName = "routine_logs")
data class RoutineLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bedtimeHour: Int,
    val bedtimeMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val sleepSeconds: Long,
    val sleepQualityScore: Int, // 1 to 100
    val dateRecorded: String // YYYY-MM-DD
)

@Entity(tableName = "ai_suggestions")
data class AISuggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val suggestionType: String, // ROUTINE, SLEEP, REMINDER
    val timestamp: Long = System.currentTimeMillis(),
    val isDismissed: Boolean = false
)
