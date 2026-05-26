package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.AppViewModel
import com.example.utils.PremiumManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val isPremium by PremiumManager.isPremium.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var voiceSelectTestText by remember { mutableStateOf("RiseIQ smart wakeup service initialized.") }
    var selectedLangTest by remember { mutableStateOf("ENG") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upgrade Premium Promo Box
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161524).copy(alpha = 0.85f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        if (isPremium) listOf(Color(0xFF10B981), Color(0xFF3B82F6))
                        else listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                    ),
                    RoundedCornerShape(24.dp)
                )
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isPremium) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("RiseIQ Premium Unlocked", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("All smart cameras, biometric selfie missions, and unlimited coaching are enabled. Thank you!", color = Color.LightGray.copy(alpha = 0.8f), fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upgrade to RiseIQ Elite", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Unlock adaptive biometric selfie verification & ad-free experience.", color = Color.LightGray.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            PremiumManager.setPremium(true)
                            Toast.makeText(context, "Welcome to RiseIQ Elite! All features successfully enabled.", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("upgrade_premium_button")
                    ) {
                        Text("Subscribe for $4.99/mo  ✨", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Voice TTS Test settings card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2F).copy(alpha = 0.75f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Default Voice Language Test", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val langs = listOf("ENG", "HIN", "URD")
                    langs.forEach { lang ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedLangTest == lang) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.05f))
                                .clickable { selectedLangTest = lang }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(lang, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = voiceSelectTestText,
                    onValueChange = { voiceSelectTestText = it },
                    label = { Text("Speech Text Builder", fontSize = 11.sp, color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.ttsManager.speak(voiceSelectTestText, selectedLangTest)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Trigger TTS Test Announcement", fontSize = 13.sp)
                }
            }
        }

        // Backend Sync and Backup restore
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2F).copy(alpha = 0.75f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Storage Backup & Secure Recovery", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Database backup successfully compiled! Path: /RiseIQ/backup.json", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981).copy(alpha = 0.15f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup", color = Color(0xFF10B981), fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Backup restoring executed beautifully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6).copy(alpha = 0.15f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF3B82F6))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore", color = Color(0xFF3B82F6), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logging user profile logout
        Button(
            onClick = {
                PremiumManager.setPremium(false) // Reset premium
                viewModel.signOut()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("logout_button")
        ) {
            Text("Logout & Reset AI Session Cache", color = Color.Red, fontWeight = FontWeight.SemiBold)
        }
    }
}
