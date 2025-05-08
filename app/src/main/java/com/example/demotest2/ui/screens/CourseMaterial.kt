package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun CourseMaterialScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Material", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("ClassSelectionScreen") // Navigate back in the stack
                    }) {
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
        bottomBar = { BottomNavigationBar(navController, "--") },
        content = { padding ->
            CourseMaterialContent(
                Modifier.padding(padding),
                onFileClick = { fileName ->
                    println("File clicked: $fileName")
                },
                onNewFileClick = {
                    println("Upload New File clicked")
                }
            )
        }
    )
}

@Composable
fun CourseMaterialContent(
    modifier: Modifier,
    onFileClick: (String) -> Unit,
    onNewFileClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF64B5F6))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Section
        Text(
            text = "Recent Uploads",
            style = TextStyle(
                color = Color(0xFF0D47A1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Recent Files Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val files = listOf("File1.pdf", "File2.pdf", "File3.pdf")
                files.forEach { file ->
                    FileItem(fileName = file, onClick = { onFileClick(file) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer Section
        Button(
            onClick = onNewFileClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
        ) {
            Text(
                text = "Upload New File",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun FileItem(fileName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fileName.firstOrNull()?.toString() ?: "",
                    style = TextStyle(
                        color = Color(0xFF1E88E5),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = fileName,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}