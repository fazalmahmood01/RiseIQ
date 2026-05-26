package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TaskReminder
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(viewModel: AppViewModel) {
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    var showCreator by remember { mutableStateOf(false) }

    // Creator inputs
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var timeStr by remember { mutableStateOf("12:00") }
    var recurrence by remember { mutableStateOf("NONE") }
    var isVoiceEnabled by remember { mutableStateOf(true) }
    var voiceText by remember { mutableStateOf("") }
    var voiceLang by remember { mutableStateOf("ENG") }

    // Recording states
    var recordedPath by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var isPlayingBack by remember { mutableStateOf(false) }

    val formatter = remember { SimpleDateFormat("h:mm a, d MMM yyyy", Locale.getDefault()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (reminders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No Habit Reminders Scheduled", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Create custom Urdu, Hindi or English voice-augmented routines below.", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 24.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reminders) { reminder ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2F).copy(alpha = 0.82f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reminder.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = reminder.description,
                                        color = Color.LightGray.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.playVoiceReminder(reminder) },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFF8B5CF6).copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Play Announcement", tint = Color(0xFF8B5CF6))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatter.format(Date(reminder.timestamp)),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(containerColor = Color(0xFF8B5CF6).copy(alpha = 0.25f), contentColor = Color(0xFF8B5CF6)) {
                                        Text(reminder.repeatType, modifier = Modifier.padding(2.dp), fontSize = 10.sp)
                                    }
                                    if (reminder.voiceLanguage.isNotEmpty()) {
                                        Badge(containerColor = Color(0xFFD4AF37).copy(alpha = 0.25f), contentColor = Color(0xFFD4AF37)) {
                                            Text(reminder.voiceLanguage, modifier = Modifier.padding(2.dp), fontSize = 10.sp)
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteReminder(reminder) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                title = ""
                desc = ""
                voiceText = ""
                recordedPath = null
                showCreator = true
            },
            containerColor = Color(0xFF8B5CF6),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .testTag("add_reminder_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Schedule Habit", tint = Color.White)
        }
    }

    if (showCreator) {
        AlertDialog(
            onDismissRequest = { showCreator = false },
            containerColor = Color(0xFF1F1D2F),
            title = { Text("Schedule Task Reminder", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Task Title", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth().testTag("reminder_title_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Brief Focus Instructions", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth().testTag("reminder_desc_input")
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = timeStr,
                                onValueChange = { timeStr = it },
                                label = { Text("Time (e.g. 18:30)", fontSize = 11.sp, color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                            // Recurrence Type dropdown simulation
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = recurrence,
                                    onValueChange = { recurrence = it.uppercase() },
                                    label = { Text("Repeat (NONE, DAILY)", fontSize = 11.sp, color = Color.Gray) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isVoiceEnabled, onCheckedChange = { isVoiceEnabled = it })
                            Text("Equip with Urdu / Hindi TTS guidance", color = Color.LightGray, fontSize = 13.sp)
                        }
                    }

                    if (isVoiceEnabled) {
                        item {
                            OutlinedTextField(
                                value = voiceText,
                                onValueChange = { voiceText = it },
                                label = { Text("Custom Announcement Speech Text", fontSize = 11.sp, color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Text("Voice Language Mode", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val languages = listOf("ENG", "HIN", "URD")
                                languages.forEach { lang ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (voiceLang == lang) Color(0xFFD4AF37) else Color.White.copy(alpha = 0.05f))
                                                .clickable { voiceLang = lang }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(lang, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                }
                            }
                        }
                    }

                    // Recording Custom Memo Option
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                            modifier = Modifier.fillModifier().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Or Record Live custom Voice memo", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            if (!isRecording) {
                                                isRecording = true
                                                val fName = "memo_${System.currentTimeMillis()}"
                                                recordedPath = viewModel.audioRecorder.startRecording(fName)
                                            } else {
                                                viewModel.audioRecorder.stopRecording()
                                                isRecording = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isRecording) Color.Red else Color(0xFFEC4899)
                                        )
                                    ) {
                                        Text(if (isRecording) "Stop 🔴" else "Record 🎙️", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = {
                                            recordedPath?.let {
                                                isPlayingBack = true
                                                viewModel.audioRecorder.startPlaying(it) {
                                                    isPlayingBack = false
                                                }
                                            }
                                        },
                                        enabled = recordedPath != null,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                    ) {
                                        Text(if (isPlayingBack) "Playing.." else "Test Play", fontSize = 11.sp)
                                    }
                                }

                                if (recordedPath != null) {
                                    Text("✅ Recording stored successfully", color = Color(0xFF10B981), fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = timeStr.substringBefore(":").toIntOrNull() ?: 12
                        val minutes = timeStr.substringAfter(":").toIntOrNull() ?: 0
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hours)
                            set(Calendar.MINUTE, minutes)
                            set(Calendar.SECOND, 0)
                            if (before(Calendar.getInstance())) {
                                add(Calendar.DATE, 1)
                            }
                        }

                        val reminder = TaskReminder(
                            title = title,
                            description = desc,
                            timestamp = calendar.timeInMillis,
                            repeatType = recurrence,
                            isVoiceReminder = isVoiceEnabled,
                            voiceText = voiceText,
                            voiceLanguage = voiceLang,
                            voiceClipPath = recordedPath
                        )
                        viewModel.saveReminder(reminder)
                        showCreator = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    modifier = Modifier.testTag("save_reminder_button")
                ) {
                    Text("Schedule routine")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreator = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

// Simple modifier fill helper extension for compatibility
private fun Modifier.fillModifier(): Modifier = this.fillMaxWidth()
