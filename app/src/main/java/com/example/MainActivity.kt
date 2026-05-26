package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.ActiveAlarmManager
import com.example.utils.NotificationHelper
import com.example.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channels for smart alarm triggers
        NotificationHelper.createNotificationChannels(this)

        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val activeRingingAlarm by ActiveAlarmManager.ringingAlarm.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F0E17) // Sleek space-midnight primary
                ) {
                    when {
                        // 1. Alarm Active Ringing State Block
                        activeRingingAlarm != null -> {
                            MissionsOverlay(
                                alarm = activeRingingAlarm!!,
                                onDismiss = {
                                    ActiveAlarmManager.dismissAlarm()
                                }
                            )
                        }

                        // 2. Auth State Block
                        currentUser == null -> {
                            AuthScreen(viewModel = viewModel)
                        }

                        // 3. Authenticated App Hub
                        else -> {
                            RiseIQAppHub(viewModel = viewModel, userName = currentUser!!.name, isPremium = currentUser!!.isPremium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiseIQAppHub(viewModel: AppViewModel, userName: String, isPremium: Boolean) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        TabItem("Hub", Icons.Default.Dashboard),
        TabItem("Alarms", Icons.Default.Alarm),
        TabItem("Habits", Icons.Default.BookmarkBorder),
        TabItem("Coaching", Icons.Default.AutoAwesome),
        TabItem("Settings", Icons.Default.Settings)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RiseIQ",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        if (isPremium) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(containerColor = Color(0xFFD4AF37), contentColor = Color.Black) {
                                Text("ELITE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF8B5CF6))
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.AccountCircle, contentDescription = userName, tint = Color.LightGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F0E17),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column {
                // 1. Interactive AdMob simulated banner if user is NOT Elite premium
                if (!isPremium) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF161524))
                            .border(1.dp, Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(containerColor = Color(0xFFD4AF37), contentColor = Color.Black) {
                                    Text("AD", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AdMob: Unlock smart biometric selfies by upgrading.",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "UPGRADE",
                                color = Color(0xFFEC4899),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        selectedTab = 4 // Navigate directly to settings screen!
                                    }
                                    .padding(4.dp)
                                    .testTag("admob_upgrade_link")
                            )
                        }
                    }
                }

                // 2. Sleek frosted Bottom Navigation
                NavigationBar(
                    containerColor = Color(0xFF0F0E17),
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF8B5CF6),
                                selectedTextColor = Color(0xFF8B5CF6),
                                indicatorColor = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F0E17), Color(0xFF1A112E))
                    )
                )
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> AlarmsScreen(viewModel = viewModel)
                2 -> RemindersScreen(viewModel = viewModel)
                3 -> SuggestionsScreen(viewModel = viewModel)
                4 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

data class TabItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
