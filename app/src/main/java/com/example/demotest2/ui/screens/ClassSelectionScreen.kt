package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demotest2.ui.theme.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelectionScreen(navController: NavController) {
    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDarkTheme) {
            listOf(BackgroundDark, Color.Black)
        } else {
            listOf(BackgroundLight, Color.White)
        }
    )

    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) Color.White else BlueTrue
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Class", color = textColor) },
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

        bottomBar = { BottomNavigationBar(navController, "ClassSelectionScreen") },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        brush = backgroundGradient
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "Select Your Class",
                    style = TextStyle(
                        color =textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // List of classes
                val classes = listOf("IT-A", "IT-B", "CSE-A", "CSE-B")
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(classes) { className ->
                        ClassCard(
                            className = className,
                            onClick = { navController.navigate("CourseMaterialScreen") }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ClassCard(
    className: String,
    onClick: () -> Unit
) {
    val textColor = if (isDarkTheme) Color.White else BlueTrue
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(if (isDarkTheme) BlueTrue else BackgroundCardLight,),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E88E5).copy(alpha = 0.1f))
        ) {
            Text(
                text = className,
                style = TextStyle(
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}