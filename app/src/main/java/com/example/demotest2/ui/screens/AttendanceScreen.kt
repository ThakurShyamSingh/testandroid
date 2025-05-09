package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demotest2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController) {
    val backgroundGradient = remember(isDarkTheme) {
        Brush.verticalGradient(
            colors = if (isDarkTheme) {
                listOf(BackgroundDark, BackgroundDarker)
            } else {
                listOf(BackgroundLight, BackgroundCardLight)
            }
        )
    }

    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) TextDark else TextLight

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                    .background(topBarColor),
                title = {
                    Text(
                        "Attendance",
                        style = TextStyle(color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MainScreen") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        },
        bottomBar = { BottomNavigationBar(navController, "AttendanceScreen") },
        content = { paddingValues ->
            AttendanceContent(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(brush = backgroundGradient)
                    .padding(16.dp),
                navController = navController
            )
        }
    )
}

@Composable
fun AttendanceContent(modifier: Modifier, navController: NavController) {
    navController.context
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Section
        Text(
            text = "Your Classes",
            style = TextStyle(
                color = if (isDarkTheme) RedAccentDark else RedAccent,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // List of Classes
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClassCard(
                title = "IT-B",
                description = "Tap to take attendance",
                backgroundColor = if (isDarkTheme) BlueTrue else BackgroundCardLight,
                onClick = { /* Handle IT-B Click */ }
            )
            ClassCard(
                title = "IT-A",
                description = "Tap to take attendance",
                backgroundColor = if (isDarkTheme) BlueTrue else BackgroundCardLight,
                onClick = { /* Handle IT-A Click */ }
            )
        }
    }
}

@Composable
fun ClassCard(
    title: String,
    description: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    var attendanceMarked = false // Placeholder for attendance status

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(backgroundColor),
        onClick = {
            onClick() // Perform the action
            attendanceMarked = !attendanceMarked // Toggle attendance status
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Content
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = if (isDarkTheme) TextDark else TextLight,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TextStyle(
                        color = (if (isDarkTheme) TextDark else TextLight).copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                )
            }

            // Mark Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background((if (isDarkTheme) TextDark else TextLight).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                // Change the icon based on attendance status
                Icon(
                    imageVector = if (attendanceMarked) Icons.Default.Check else Icons.Default.CameraRoll,
                    contentDescription = if (attendanceMarked) "Attendance Marked" else "Mark Attendance",
                    tint = if (attendanceMarked) GreenAccent else TextLight
                )
            }
        }
    }
}
