package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {

    val allAlarms: Flow<List<Alarm>> = database.alarmDao().getAllAlarms()
    val allReminders: Flow<List<TaskReminder>> = database.reminderDao().getAllReminders()
    val recentRoutineLogs: Flow<List<RoutineLog>> = database.routineDao().getRecentLogs()
    val activeSuggestions: Flow<List<AISuggestion>> = database.suggestionDao().getActiveSuggestions()

    suspend fun insertAlarm(alarm: Alarm): Long {
        return database.alarmDao().insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        database.alarmDao().updateAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        database.alarmDao().deleteAlarm(alarm)
    }

    suspend fun getAlarmById(id: Int): Alarm? {
        return database.alarmDao().getAlarmById(id)
    }

    suspend fun insertReminder(reminder: TaskReminder): Long {
        return database.reminderDao().insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: TaskReminder) {
        database.reminderDao().updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: TaskReminder) {
        database.reminderDao().deleteReminder(reminder)
    }

    suspend fun insertRoutineLog(log: RoutineLog) {
        database.routineDao().insertLog(log)
    }

    suspend fun insertSuggestion(suggestion: AISuggestion) {
        database.suggestionDao().insertSuggestion(suggestion)
    }

    suspend fun dismissSuggestion(id: Int) {
        database.suggestionDao().dismissSuggestion(id)
    }
}
