package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnovationsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Innovations", color = Color.White) },
                navigationIcon = {
                    IconButton({navController.navigate("MainScreen")}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, "InnovationsScreen")
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))
                        )
                    )
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Section
                    Text(
                        text = "Explore Innovations",
                        style = TextStyle(
                            color = Color(0xFF0D47A1),
                            fontSize = 24.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    )

                    // New Ideas Section
                    InnovationsCard(
                        title = "New Ideas",
                        description = "Explore the latest ideas and innovations.",
                        onClick = {
//                            navController.navigate("NewIdeas")
                        }
                    )

                    // Approved Ideas Section
                    InnovationsCard(
                        title = "Approved Ideas",
                        description = "View innovations that have been approved.",
                        onClick = {
//                            navController.navigate("Approved Ideas")
                        }

                    )
                }
            }
        }
    )
}

@Composable
fun InnovationsCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            )
        }
    }
}