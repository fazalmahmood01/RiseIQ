package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Alarm
import com.example.utils.ActiveAlarmManager

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MissionsOverlay(alarm: Alarm, onDismiss: () -> Unit) {
    var stepPhase by remember { mutableStateOf(1) } // 1: Introduction, 2: Active Task, 3: Completed Successfully
    val ringtoneName = alarm.ringtoneName

    val gradientBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F0F3D), // Cosmic deep violet
            Color(0xFF0F0720),
            Color(0xFF240E1B)  // Pulsing magenta
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (stepPhase == 1) {
                // Intro / Alarm Warning Phase
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = alarm.label,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Smart Mission Verification Required to Dismiss",
                    color = Color.LightGray.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Mission specific details information
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ACTIVE REQUIREMENT: " + alarm.missionType,
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (alarm.missionType) {
                                "MATH" -> "Solve 3 increasingly complex math equations to awaken your logical brain core."
                                "SHAKE" -> "Energetically shake your smartphone device to boost instant kinetic circulation."
                                "QR" -> "Physically walk and align the smart barcode scanner overlay on your morning routine targets."
                                "WALK" -> "Rise, pace and record 15 physical steps to prove spatial awake feedback."
                                "SELFIE" -> "Nod, look directly into the camera system and snap a smile to verify biological state."
                                else -> "Simple touch screen dismiss alert verification."
                            },
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (alarm.missionType == "NONE") {
                            stepPhase = 3
                        } else {
                            stepPhase = 2
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_mission_button")
                ) {
                    Text("START MISSION CHALLENGE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

            } else if (stepPhase == 2) {
                // Interactive Gameplay Phase based on Mission
                MissionInteractiveTask(
                    missionType = alarm.missionType,
                    onSuccess = { stepPhase = 3 }
                )
            } else if (stepPhase == 3) {
                // Success / Welcome to the day phase
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(84.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "AWAKENING COMPLETE",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your prefrontal cortex is fully synced. Have a spectacular, productive day ahead!",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dismiss_alarm_overlay_button")
                ) {
                    Text("Enter Productivity Hub", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MissionInteractiveTask(missionType: String, onSuccess: () -> Unit) {
    when (missionType) {
        "MATH" -> MathMissionTask(onSuccess)
        "SHAKE" -> ShakeMissionTask(onSuccess)
        "QR" -> QRMissionTask(onSuccess)
        "WALK" -> WalkMissionTask(onSuccess)
        "SELFIE" -> SelfieMissionTask(onSuccess)
        else -> {
            Button(onClick = onSuccess) { Text("Solve") }
        }
    }
}

@Composable
fun MathMissionTask(onSuccess: () -> Unit) {
    var problemIndex by remember { mutableStateOf(1) }
    val equations = listOf("14 + 27", "8 * 9", "124 - 45")
    val answers = listOf(41, 72, 79)

    var currentAnswerInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Logical Awakening Mode", color = Color(0xFFD4AF37), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Solve equation $problemIndex of 3:", color = Color.LightGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = equations[problemIndex - 1],
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = currentAnswerInput,
            onValueChange = { currentAnswerInput = it; errorMessage = "" },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF8B5CF6)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("math_input_field")
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val parsed = currentAnswerInput.toIntOrNull()
                if (parsed == answers[problemIndex - 1]) {
                    if (problemIndex < 3) {
                        problemIndex++
                        currentAnswerInput = ""
                    } else {
                        onSuccess()
                    }
                } else {
                    errorMessage = "Incorrect answer, recount and retry!"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("math_submit_button")
        ) {
            Text("CHECK EQUATION", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ShakeMissionTask(onSuccess: () -> Unit) {
    var shakePercent by remember { mutableStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Kinetic Awakening Mode", color = Color(0xFFD4AF37), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
                .border(2.dp, Color(0xFFEC4899), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$shakePercent%", fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("Shake Intensity", fontSize = 12.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        LinearProgressIndicator(
            progress = { shakePercent / 100f },
            color = Color(0xFFEC4899),
            trackColor = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (shakePercent < 100) {
                    shakePercent += 20
                    if (shakePercent >= 100) {
                        onSuccess()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("sim_shake_button")
        ) {
            Text("SIMULATE KINETIC SHAKE  📳", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QRMissionTask(onSuccess: () -> Unit) {
    var scanCompleted by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Routine Target Alignment Scanner", color = Color(0xFFD4AF37), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(200.dp)
                .border(2.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(140.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Position target QR code within the scan borders", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onSuccess,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("sim_scan_qr_button")
        ) {
            Text("SIMULATE SUCCESSFUL BARCODE SCAN", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WalkMissionTask(onSuccess: () -> Unit) {
    var stepCounter by remember { mutableStateOf(0) }
    var lastFoot by remember { mutableStateOf("LEFT") } // LEFT or RIGHT to enforce active walking alternating taps!

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pedometer Spatial Awakening", color = Color(0xFFD4AF37), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("$stepCounter / 15", fontSize = 54.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text("Required Morning Steps", color = Color.LightGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (lastFoot == "RIGHT") {
                        stepCounter++
                        lastFoot = "LEFT"
                        if (stepCounter >= 15) onSuccess()
                    }
                },
                enabled = lastFoot == "RIGHT",
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                modifier = Modifier.weight(1f).height(50.dp).testTag("sim_left_step")
            ) {
                Text("Left Step 👣", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (lastFoot == "LEFT") {
                        stepCounter++
                        lastFoot = "RIGHT"
                        if (stepCounter >= 15) onSuccess()
                    }
                },
                enabled = lastFoot == "LEFT",
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                modifier = Modifier.weight(1f).height(50.dp).testTag("sim_right_step")
            ) {
                Text("Right Step 👣", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SelfieMissionTask(onSuccess: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Biometric Face Verify Awakening", color = Color(0xFFD4AF37), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
                .border(2.dp, Color(0xFF3B82F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Viewfinder Mockup
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Live Biometric View", fontSize = 11.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Smile direct to trigger biometric capture checks.", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onSuccess,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("sim_selfie_button")
        ) {
            Text("MOCK SMILE BIO SNAP ENVELOPE", fontWeight = FontWeight.Bold)
        }
    }
}
