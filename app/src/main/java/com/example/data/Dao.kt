package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Int): Alarm?
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY timestamp ASC")
    fun getAllReminders(): Flow<List<TaskReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: TaskReminder): Long

    @Update
    suspend fun updateReminder(reminder: TaskReminder)

    @Delete
    suspend fun deleteReminder(reminder: TaskReminder)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_logs ORDER BY dateRecorded DESC LIMIT 30")
    fun getRecentLogs(): Flow<List<RoutineLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RoutineLog)
}

@Dao
interface SuggestionDao {
    @Query("SELECT * FROM ai_suggestions WHERE isDismissed = 0 ORDER BY timestamp DESC")
    fun getActiveSuggestions(): Flow<List<AISuggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: AISuggestion)

    @Query("UPDATE ai_suggestions SET isDismissed = 1 WHERE id = :id")
    suspend fun dismissSuggestion(id: Int)
}
