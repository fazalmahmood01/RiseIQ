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
import com.example.data.Alarm
import com.example.viewmodel.AppViewModel
import com.example.utils.PremiumManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen(viewModel: AppViewModel) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val isPremium by PremiumManager.isPremium.collectAsStateWithLifecycle()
    var showConfigurator by remember { mutableStateOf(false) }
    var selectedAlarmForEdit by remember { mutableStateOf<Alarm?>(null) }

    // Alarm details state
    var editLabel by remember { mutableStateOf("") }
    var editHour by remember { mutableStateOf("07") }
    var editMinute by remember { mutableStateOf("00") }
    var editMissionType by remember { mutableStateOf("MATH") }
    var editDifficulty by remember { mutableStateOf("MEDIUM") }
    var editLoudMode by remember { mutableStateOf(false) }
    var editvibrate by remember { mutableStateOf(true) }
    var editFlashlight by remember { mutableStateOf(false) }
    var editRingtoneName by remember { mutableStateOf("Radiant Pulse") }
    val selectedDays = remember { mutableStateListOf<String>() }

    fun openForEditing(alarm: Alarm?) {
        selectedAlarmForEdit = alarm
        if (alarm != null) {
            editLabel = alarm.label
            editHour = String.format("%02d", alarm.hour)
            editMinute = String.format("%02d", alarm.minute)
            editMissionType = alarm.missionType
            editDifficulty = alarm.missionDifficulty
            editLoudMode = alarm.isLoudMode
            editvibrate = alarm.vibrate
            editFlashlight = alarm.flashlight
            editRingtoneName = alarm.ringtoneName
            selectedDays.clear()
            if (alarm.recurringDays.isNotEmpty()) {
                selectedDays.addAll(alarm.recurringDays.split(","))
            }
        } else {
            editLabel = "Rise & Shine"
            editHour = "07"
            editMinute = "00"
            editMissionType = "MATH"
            editDifficulty = "MEDIUM"
            editLoudMode = false
            editvibrate = true
            editFlashlight = false
            editRingtoneName = "Radiant Pulse"
            selectedDays.clear()
        }
        showConfigurator = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (alarms.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Snooze,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No Alarms Scheduled", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tap the floating button below to customize and launch your first smart alarm.", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 24.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(alarms) { alarm ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2F).copy(alpha = 0.82f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                            .clickable { openForEditing(alarm) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${String.format("%02d", alarm.hour)}:${String.format("%02d", alarm.minute)}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (alarm.label.isNotEmpty()) alarm.label else "Alarm",
                                        color = Color.LightGray,
                                        fontSize = 13.sp
                                    )
                                }
                                Switch(
                                    checked = alarm.isEnabled,
                                    onCheckedChange = { viewModel.toggleAlarm(alarm) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF8B5CF6),
                                        checkedTrackColor = Color(0xFF8B5CF6).copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Recurrence days tags
                                    val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                    val alarmDays = alarm.recurringDays.split(",")
                                    daysList.forEach { day ->
                                        val isSet = alarmDays.contains(day)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSet) Color(0xFF8B5CF6).copy(alpha = 0.25f) else Color.Transparent)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = day.first().toString(),
                                                color = if (isSet) Color(0xFF8B5CF6) else Color.Gray,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = when(alarm.missionType) {
                                            "NONE" -> Icons.Default.Circle
                                            "MATH" -> Icons.Default.PlusOne
                                            "SHAKE" -> Icons.Default.Vibration
                                            "QR" -> Icons.Default.QrCodeScanner
                                            "WALK" -> Icons.Default.DirectionsWalk
                                            "SELFIE" -> Icons.Default.PhotoCamera
                                            else -> Icons.Default.Alarm
                                        },
                                        contentDescription = null,
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (alarm.missionType == "NONE") "Direct Sleep" else "${alarm.missionType} Mission",
                                        color = Color(0xFFD4AF37),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
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

        // FAB to show design creator
        FloatingActionButton(
            onClick = { openForEditing(null) },
            containerColor = Color(0xFF8B5CF6),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .testTag("add_alarm_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Alarm", tint = Color.White)
        }
    }

    if (showConfigurator) {
        AlertDialog(
            onDismissRequest = { showConfigurator = false },
            containerColor = Color(0xFF1F1D2F),
            title = {
                Text(
                    text = if (selectedAlarmForEdit == null) "Configure Smart Alarm" else "Modify Alarm Settings",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editHour,
                                onValueChange = { editHour = it },
                                label = { Text("Hour (0-23)", fontSize = 11.sp, color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f).testTag("alarm_hour_input")
                            )
                            OutlinedTextField(
                                value = editMinute,
                                onValueChange = { editMinute = it },
                                label = { Text("Minute (0-59)", fontSize = 11.sp, color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f).testTag("alarm_minute_input")
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = editLabel,
                            onValueChange = { editLabel = it },
                            label = { Text("Alarm Label", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth().testTag("alarm_label_input")
                        )
                    }

                    // Recurring Days setup
                    item {
                        Text("Repeat days schedule", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            days.forEach { day ->
                                val active = selectedDays.contains(day)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.05f))
                                        .clickable {
                                            if (active) selectedDays.remove(day) else selectedDays.add(day)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(day.first().toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Wakeup mission picker
                    item {
                        Text("Wake-up Mission Verification", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val missions = listOf("NONE", "MATH", "SHAKE", "QR", "WALK", "SELFIE")
                            missions.take(3).forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .weight(1.dp.value)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (editMissionType == m) Color(0xFFD4AF37) else Color.White.copy(alpha = 0.05f))
                                        .clickable {
                                            if (m == "SELFIE" && !isPremium) {
                                                // Handle premium check but allow selection
                                            }
                                            editMissionType = m
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(m, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val missions = listOf("NONE", "MATH", "SHAKE", "QR", "WALK", "SELFIE")
                            missions.drop(3).forEach { m ->
                                val itemPremiumText = if (m == "SELFIE") " (⭐)" else ""
                                Box(
                                    modifier = Modifier
                                        .weight(1.dp.value)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (editMissionType == m) Color(0xFFD4AF37) else Color.White.copy(alpha = 0.05f))
                                        .clickable {
                                            editMissionType = m
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$m$itemPremiumText", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Extra configurations
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = editLoudMode, onCheckedChange = { editLoudMode = it })
                            Text("Increasing smart volume & Loud Mode", color = Color.LightGray, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = editvibrate, onCheckedChange = { editvibrate = it })
                            Text("Intensity Vibration alarm feedback", color = Color.LightGray, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = editFlashlight, onCheckedChange = { editFlashlight = it })
                            Text("Pulsing flashlight strobe guidance", color = Color.LightGray, fontSize = 13.sp)
                        }
                    }

                    // Ringtone Picker
                    item {
                        Text("Custom Ringtone Selector", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val ringtones = listOf("Radiant Pulse", "Digital Sunset", "Synth Horizon")
                            ringtones.forEach { r ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (editRingtoneName == r) Color(0xFF10B981) else Color.White.copy(alpha = 0.05f))
                                        .clickable { editRingtoneName = r }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(r, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalAlarm = Alarm(
                            id = selectedAlarmForEdit?.id ?: 0,
                            hour = editHour.toIntOrNull() ?: 7,
                            minute = editMinute.toIntOrNull() ?: 0,
                            label = editLabel,
                            isEnabled = selectedAlarmForEdit?.isEnabled ?: true,
                            recurringDays = selectedDays.joinToString(","),
                            missionType = editMissionType,
                            missionDifficulty = editDifficulty,
                            isLoudMode = editLoudMode,
                            vibrate = editvibrate,
                            flashlight = editFlashlight,
                            ringtoneName = editRingtoneName
                        )
                        viewModel.saveAlarm(finalAlarm)
                        showConfigurator = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    modifier = Modifier.testTag("save_alarm_button")
                ) {
                    Text("Save Alarm")
                }
            },
            dismissButton = {
                Row {
                    if (selectedAlarmForEdit != null) {
                        IconButton(
                            onClick = {
                                viewModel.deleteAlarm(selectedAlarmForEdit!!)
                                showConfigurator = false
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                    TextButton(onClick = { showConfigurator = false }) {
                        Text("Dismiss", color = Color.Gray)
                    }
                }
            }
        )
    }
}
