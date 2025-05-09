package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.demotest2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseMaterialScreen(navController: NavController) {


    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) Color.White else BlueTrue
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Material", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("ClassSelectionScreen") // Navigate back in the stack
                    }) {
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
    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDarkTheme) {
            listOf(BackgroundDark, Color.Black)
        } else {
            listOf(BackgroundLight, Color.White)
        }
    )

    val textColor = if (isDarkTheme) Color.White else BlueTrue
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush =backgroundGradient,
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
                color = textColor,
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
            colors = CardDefaults.cardColors(if (isDarkTheme) BlueTrue else BackgroundCardLight,)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .height(100.dp),

                ) {
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