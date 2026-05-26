package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AppViewModel) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F0C20), // Dark space base
            Color(0xFF1A103C), // Cosmic violet
            Color(0xFF0F0C20)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            // Cosmic Pulsing Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFD4AF37), Color(0x00D4AF37))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏰",
                    fontSize = 50.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RiseIQ",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Neuro-Scientific Smart Wake & Habit coach",
                fontSize = 14.sp,
                color = Color.LightGray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Glassmorphic Input Field Wrapper
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1F1D2F).copy(alpha = 0.75f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.02f))
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUp) "Create AI Profile" else "Access Hub",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Your Name", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_name_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Simulated Google Sign-In button
                    Button(
                        onClick = {
                            viewModel.signInWithGoogle(email, if (isSignUp) name else email.substringBefore("@"))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_signin_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🔑  Continue with Google-ID",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isSignUp) "Already registered? Login" else "New to RiseIQ? Establish Profile",
                        color = Color(0xFFD4AF37),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { isSignUp = !isSignUp }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Guest Entry Option
            TextButton(
                onClick = {
                    viewModel.signInWithGoogle("guest@riseiq.ai", "RiseIQ Guest")
                },
                modifier = Modifier.testTag("auth_guest_button")
            ) {
                Text(
                    text = "No Account? Enter offline guest mode",
                    color = Color.LightGray.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
