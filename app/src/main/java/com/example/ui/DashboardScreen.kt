package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.AppViewModel
import com.example.utils.WeatherClient
import com.example.utils.GeminiClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val nextAlarm by viewModel.nextAlarmTimeMessage.collectAsStateWithLifecycle()
    val weather by WeatherClient.weather.collectAsStateWithLifecycle()
    val routineLogs by viewModel.routineLogs.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showSleepLogger by remember { mutableStateOf(false) }
    var currentQuote by remember { mutableStateOf("\"The only way to do great work is to love what you do.\" - Steve Jobs") }
    var isFetchingQuote by remember { mutableStateOf(false) }

    var citySearchQuery by remember { mutableStateOf("") }
    var showCityInput by remember { mutableStateOf(false) }

    // Sleep logging inputs
    var bedtimeHour by remember { mutableStateOf("22") }
    var bedtimeMinute by remember { mutableStateOf("30") }
    var wakeHour by remember { mutableStateOf("06") }
    var wakeMinute by remember { mutableStateOf("30") }
    var feltRested by remember { mutableStateOf(true) }

    val avgSleepScore = remember(routineLogs) {
        if (routineLogs.isEmpty()) 80
        else routineLogs.map { it.sleepQualityScore }.average().toInt()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Overview
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1F1D2F).copy(alpha = 0.85f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.02f))
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Next Wake-Up Mission",
                            fontSize = 13.sp,
                            color = Color.LightGray.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nextAlarm,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Quote & AI Generator Box
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF161524).copy(alpha = 0.75f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mental Energy Suggestion",
                            fontSize = 12.sp,
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                if (!isFetchingQuote) {
                                    isFetchingQuote = true
                                    scope.launch {
                                        currentQuote = GeminiClient.generateText(
                                            prompt = "Suggest an inspiring short morning quote about discipline, focus, or awakening.",
                                            systemInstruction = "You are a zen master, express with pure elegance."
                                        )
                                        isFetchingQuote = false
                                    }
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            if (isFetchingQuote) {
                                CircularProgressIndicator(color = Color(0xFFD4AF37), strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.Default.Psychology, contentDescription = "Synergy", tint = Color(0xFFD4AF37), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentQuote,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Two Column row for Sleep and Weather
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sleep Quality Score Box (Glassmorphic Gauge)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2F).copy(alpha = 0.75f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(190.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sleep Quality",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { avgSleepScore / 100f },
                                color = Color(0xFFEC4899),
                                strokeWidth = 8.dp,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "$avgSleepScore%",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Log Cycle",
                            color = Color(0xFFEC4899),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { showSleepLogger = true }
                                .padding(4.dp)
                        )
                    }
                }

                // Interactive Weather Widget Box
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2F).copy(alpha = 0.75f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(190.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = weather.city,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.EditLocation,
                                contentDescription = "Change Location",
                                tint = Color.LightGray,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showCityInput = !showCityInput }
                            )
                        }

                        if (showCityInput) {
                            OutlinedTextField(
                                value = citySearchQuery,
                                onValueChange = { citySearchQuery = it },
                                placeholder = { Text("City", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    if (citySearchQuery.isNotEmpty()) {
                                        WeatherClient.updateCity(citySearchQuery)
                                        citySearchQuery = ""
                                        showCityInput = false
                                    }
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF8B5CF6)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            )
                        } else {
                            Text(
                                text = weather.icon,
                                fontSize = 42.sp
                            )
                        }

                        Text(
                            text = "${weather.temp}°F - ${weather.condition}",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Daily sleep logger visual summary table
        item {
            Text(
                text = "Sleep Routine Log History",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (routineLogs.isEmpty()) {
            item {
                Text(
                    text = "No recorded logs yet. Tap 'Log Cycle' above to enter sleep records manually.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        } else {
            items(routineLogs.take(3)) { log ->
                val hours = log.sleepSeconds / 3600.0
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161524).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Logged Date: ${log.dateRecorded}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Duration: ${String.format("%.1f", hours)} hrs  (${log.bedtimeHour}:${String.format("%02d", log.bedtimeMinute)} to ${log.wakeHour}:${String.format("%02d", log.wakeMinute)})",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Badge(
                            containerColor = if (log.sleepQualityScore >= 80) Color(0xFF10B981) else Color(0xFFFBBF24),
                            contentColor = Color.Black
                        ) {
                            Text(
                                text = "Score: ${log.sleepQualityScore}%",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Dummy spacing for floating button overlays
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Sleep logging dialog
    if (showSleepLogger) {
        AlertDialog(
            onDismissRequest = { showSleepLogger = false },
            containerColor = Color(0xFF1F1D2F),
            title = { Text("Log Bedtime & Waking Track", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Help RiseIQ AI study your sleep limits to automate routines.", color = Color.LightGray, fontSize = 12.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bedtimeHour,
                            onValueChange = { bedtimeHour = it },
                            label = { Text("Bed Hour (0-23)", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bedtimeMinute,
                            onValueChange = { bedtimeMinute = it },
                            label = { Text("Bed Min (0-59)", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = wakeHour,
                            onValueChange = { wakeHour = it },
                            label = { Text("Wake Hour (0-23)", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = wakeMinute,
                            onValueChange = { wakeMinute = it },
                            label = { Text("Wake Min (0-59)", fontSize = 11.sp, color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = feltRested,
                            onCheckedChange = { feltRested = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF8B5CF6))
                        )
                        Text("Did you feel fully rested upon waking?", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bh = bedtimeHour.toIntOrNull() ?: 22
                        val bm = bedtimeMinute.toIntOrNull() ?: 30
                        val wh = wakeHour.toIntOrNull() ?: 6
                        val wm = wakeMinute.toIntOrNull() ?: 30
                        viewModel.logSleepAndRate(bh, bm, wh, wm, feltRested)
                        showSleepLogger = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("Save Log Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepLogger = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
