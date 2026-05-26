package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
    val isPremium: Boolean = false
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    // Database flows
    val alarms: StateFlow<List<Alarm>>
    val reminders: StateFlow<List<TaskReminder>>
    val routineLogs: StateFlow<List<RoutineLog>>
    val activeSuggestions: StateFlow<List<AISuggestion>>

    // Session State
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser

    // TTS & Recorder Helpers
    val ttsManager = TTSManager(application)
    val audioRecorder = AudioRecorder(application)

    // UI States
    private val _isGeneratingSuggestions = MutableStateFlow(false)
    val isGeneratingSuggestions: StateFlow<Boolean> = _isGeneratingSuggestions

    private val _nextAlarmTimeMessage = MutableStateFlow("No upcoming alarms")
    val nextAlarmTimeMessage: StateFlow<String> = _nextAlarmTimeMessage

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)

        alarms = repository.allAlarms.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        reminders = repository.allReminders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        routineLogs = repository.recentRoutineLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeSuggestions = repository.activeSuggestions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Prepopulate empty database with polished samples
        viewModelScope.launch {
            alarms.collectLatest { list ->
                if (list.isEmpty()) {
                    val sample1 = Alarm(
                        hour = 6,
                        minute = 30,
                        label = "Rise & Shine",
                        missionType = "MATH",
                        missionDifficulty = "MEDIUM",
                        ringtoneName = "Radiant Pulse"
                    )
                    val sample2 = Alarm(
                        hour = 7,
                        minute = 45,
                        label = "Gym Wakeup",
                        missionType = "SHAKE",
                        ringtoneName = "Synth Horizon"
                    )
                    repository.insertAlarm(sample1)
                    repository.insertAlarm(sample2)
                }
            }
        }

        viewModelScope.launch {
            reminders.collectLatest { list ->
                if (list.isEmpty()) {
                    val sample1 = TaskReminder(
                        title = "Take Daily Vitamin D",
                        description = "Required morning routine",
                        timestamp = System.currentTimeMillis() + 3600000 * 2,
                        isVoiceReminder = true,
                        voiceText = "Assalamu Alaikum, please take your Vitamin D tablet now.",
                        voiceLanguage = "ENG"
                    )
                    repository.insertReminder(sample1)
                }
            }
        }

        viewModelScope.launch {
            routineLogs.collectLatest { list ->
                if (list.isEmpty()) {
                    // Populate past 3 days logs
                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val baseTime = System.currentTimeMillis()
                    repository.insertRoutineLog(RoutineLog(
                        bedtimeHour = 22, bedtimeMinute = 30,
                        wakeHour = 6, wakeMinute = 30,
                        sleepSeconds = 8 * 3600, sleepQualityScore = 85,
                        dateRecorded = fmt.format(Date(baseTime - 86400000))
                    ))
                    repository.insertRoutineLog(RoutineLog(
                        bedtimeHour = 23, bedtimeMinute = 15,
                        wakeHour = 7, wakeMinute = 0,
                        sleepSeconds = 7.75.toLong() * 3600, sleepQualityScore = 78,
                        dateRecorded = fmt.format(Date(baseTime - 86400000 * 2))
                    ))
                }
            }
        }

        // Keep Premium state synced with user
        viewModelScope.launch {
            PremiumManager.isPremium.collect { isPrem ->
                val user = _currentUser.value
                if (user != null) {
                    _currentUser.value = user.copy(isPremium = isPrem)
                }
            }
        }

        // Calculate next alarm message
        viewModelScope.launch {
            alarms.collectLatest { list ->
                val enabled = list.filter { it.isEnabled }
                if (enabled.isEmpty()) {
                    _nextAlarmTimeMessage.value = "No upcoming alarms"
                } else {
                    val next = enabled.minBy { it.hour * 60 + it.minute }
                    _nextAlarmTimeMessage.value = "Next Alarm: ${String.format("%02d:%02d", next.hour, next.minute)}"
                }
            }
        }
    }

    // Alarms
    fun saveAlarm(alarm: Alarm) {
        viewModelScope.launch {
            if (alarm.id == 0) {
                val idLong = repository.insertAlarm(alarm)
                ActiveAlarmManager.scheduleAlarm(getApplication(), alarm.copy(id = idLong.toInt()))
            } else {
                repository.updateAlarm(alarm)
                ActiveAlarmManager.scheduleAlarm(getApplication(), alarm)
            }
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            repository.updateAlarm(updated)
            if (updated.isEnabled) {
                ActiveAlarmManager.scheduleAlarm(getApplication(), updated)
            } else {
                ActiveAlarmManager.cancelAlarm(getApplication(), updated)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
            ActiveAlarmManager.cancelAlarm(getApplication(), alarm)
        }
    }

    // Reminders
    fun saveReminder(reminder: TaskReminder) {
        viewModelScope.launch {
            if (reminder.id == 0) {
                repository.insertReminder(reminder)
            } else {
                repository.updateReminder(reminder)
            }
        }
    }

    fun deleteReminder(reminder: TaskReminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    // Sleep Logging & Scoring
    fun logSleepAndRate(bedHour: Int, bedMin: Int, wakeHour: Int, wakeMin: Int, rested: Boolean) {
        viewModelScope.launch {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = format.format(Date())

            // Compute length of sleep
            var bedMinutes = bedHour * 60 + bedMin
            var wakeMinutes = wakeHour * 60 + wakeMin
            if (wakeMinutes < bedMinutes) {
                wakeMinutes += 24 * 60 // Next day
            }
            val durationMinutes = wakeMinutes - bedMinutes
            val durationSeconds = durationMinutes.toLong() * 60

            // Base score based on ideal 8 hours (480 mins)
            val deviation = kotlin.math.abs(480 - durationMinutes)
            var score = 100 - (deviation / 10 * 3)
            if (!rested) score -= 15
            score = score.coerceIn(40, 100)

            val log = RoutineLog(
                bedtimeHour = bedHour,
                bedtimeMinute = bedMin,
                wakeHour = wakeHour,
                wakeMinute = wakeMin,
                sleepSeconds = durationSeconds,
                sleepQualityScore = score,
                dateRecorded = dateStr
            )
            repository.insertRoutineLog(log)
            generateAISuggestions() // Re-evaluate routine matching sleep metrics!
        }
    }

    // AI Suggestions
    fun generateAISuggestions() {
        if (_isGeneratingSuggestions.value) return
        _isGeneratingSuggestions.value = true
        viewModelScope.launch {
            try {
                // Construct a contextual prompt based on recent limits
                val alarmList = alarms.value
                val routineList = routineLogs.value
                val queryStr = buildString {
                    append("Analyse this user daily routine and provide smart habit adjustment tips.\n")
                    append("Current Alarms: \n")
                    alarmList.forEach { append("- ${it.label} at ${it.hour}:${it.minute} with mission ${it.missionType}\n") }
                    append("\nRecent sleep score details: \n")
                    routineList.take(3).forEach { append("- Bedtime: ${it.bedtimeHour}:${it.bedtimeMinute}, Wake: ${it.wakeHour}:${it.wakeMinute}, Sleep Quality: ${it.sleepQualityScore}%\n") }
                    append("\nRespond strictly in valid JSON format only, enclosing a list of suggestions. Schema: ")
                    append("""[{"title": "Suggested action title", "content": "Comprehensive details explaining why and how to execute this", "suggestionType": "SLEEP" | "REMINDER" | "ROUTINE"}]""")
                }

                val resultText = GeminiClient.generateText(
                    prompt = queryStr,
                    systemInstruction = "You are RiseIQ AI coach, a world-class sleep hygienist and behavior economist. You must only answer with valid raw JSON arrays fitting the requested schema."
                )

                try {
                    val cleanedJson = resultText.trim()
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()

                    val jsonArray = JSONArray(cleanedJson)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        repository.insertSuggestion(AISuggestion(
                            title = obj.getString("title"),
                            content = obj.getString("content"),
                            suggestionType = obj.getString("suggestionType")
                        ))
                    }
                } catch (jsonEx: Exception) {
                    // Fallback to parsed elements if JSON format was slightly off
                    repository.insertSuggestion(AISuggestion(
                        title = "Align Your Sleep Routine",
                        content = "Set consistent alarms using RiseIQ missions to establish strong neural pathways during morning awakenings.",
                        suggestionType = "ROUTINE"
                    ))
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isGeneratingSuggestions.value = false
            }
        }
    }

    fun dismissSuggestion(id: Int) {
        viewModelScope.launch {
            repository.dismissSuggestion(id)
        }
    }

    // Voice Reminder playback/recording
    fun playVoiceReminder(reminder: TaskReminder) {
        if (reminder.voiceClipPath != null) {
            audioRecorder.startPlaying(reminder.voiceClipPath)
        } else if (reminder.voiceText.isNotEmpty()) {
            ttsManager.speak(reminder.voiceText, reminder.voiceLanguage)
        }
    }

    // Authentication Simulation
    fun signInWithGoogle(email: String, name: String) {
        val emailNormalized = email.trim().ifEmpty { "user@example.com" }
        val nameNormalized = name.trim().ifEmpty { "Guest User" }
        _currentUser.value = UserProfile(
            id = "google_" + System.currentTimeMillis(),
            name = nameNormalized,
            email = emailNormalized,
            isPremium = PremiumManager.isPremium.value
        )
    }

    fun signOut() {
        _currentUser.value = null
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        audioRecorder.stopPlaying()
        audioRecorder.stopRecording()
    }
}
