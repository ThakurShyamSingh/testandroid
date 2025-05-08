package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MainScreen") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        bottomBar = { BottomNavigationBar(navController, "AttendanceScreen") },
        content = { padding ->
            AttendanceContent(
                modifier = Modifier.padding(padding),
                navController = navController
            )
        }
    )
}

@Composable
fun AttendanceContent(modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8)) // Matching gradient with MainScreen
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Section
        Text(
            text = "Your Classes",
            style = TextStyle(
                color = Color(0xFF388E3C),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // List of Classes
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClassCard1(
                title = "IT-B",
                description = "Tap to take attendance",
                backgroundColor = Color(0xFF66BB6A),
                onClick = { /* Handle IT-B Click */ }
            )
            ClassCard1(
                title = "IT-A",
                description = "Tap to take attendance",
                backgroundColor = Color(0xFF4CAF50),
                onClick = { /* Handle IT-A Click */ }
            )
        }
    }
}

@Composable
fun ClassCard1(
    title: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), // Matching card height with MainScreen
        shape = RoundedCornerShape(24.dp), // Rounded corners for consistency
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
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
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                )
            }

            // Mark Icon
            Box(
                modifier = Modifier
                    .size(48.dp) // Slightly larger button
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)), // Subtle button background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Mark Attendance",
                    tint = Color.White
                )
            }
        }
    }
}