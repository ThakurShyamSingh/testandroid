package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraRoll
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
fun ProfileScreen(navController: NavController) {
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
                    .background(topBarColor)
                    .shadow(4.dp, shape = RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)),
                title = {
                    Text(
                        "Profile",
                        style = TextStyle(color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
        bottomBar = { BottomNavigationBar(navController, "ProfileScreen") },
        content = { paddingValues ->
            ProfileContent(
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
fun ProfileContent(modifier: Modifier, navController: NavController) {
    navController.context
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Profile Picture Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(BlueTrue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraRoll,
                contentDescription = "Profile Picture",
                tint = TextLight,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Information Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "John Doe",
                style = TextStyle(color = TextLight, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Email: johndoe@example.com",
                style = TextStyle(color = TextLight.copy(alpha = 0.7f), fontSize = 16.sp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Button
        Button(
            onClick = { /* Edit Profile Action */ },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
        ) {
            Text(
                text = "Edit Profile",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = TextLight
            )
        }
    }
}
