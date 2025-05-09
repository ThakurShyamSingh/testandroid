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
fun ClubsScreen(navController: NavController) {


    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) Color.White else BlueTrue

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clubs", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(topBarColor)
            )
        },
        bottomBar = { BottomNavigationBar(navController, "--") },
        content = { padding ->
            ClubsContent(
                modifier = Modifier.padding(padding)
            )
        }
    )
}

@Composable
fun ClubsContent(modifier: Modifier) {
    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDarkTheme) {
            listOf(BackgroundDark, Color.Black)
        } else {
            listOf(BackgroundLight, Color.White)
        }
    )

    val clubs = listOf("Akrithi Club", "Lexis Club", "Sports Club") // List of clubs

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = backgroundGradient
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title
        Text(
            text = "Explore Our Clubs",
            style = TextStyle(
                color = Color(0xFF0D47A1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // List of Clubs
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(clubs) { club ->
                ClubCard(clubName = club, onAddClick = { /* Handle Add Club */ })
            }
        }
    }
}

@Composable
fun ClubCard(
    clubName: String,
    onAddClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor =(if (isDarkTheme) BlueTrue else BackgroundCardLight)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = clubName,
                style = TextStyle(
                    color = if (isDarkTheme) TextDark else TextLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            TextButton(onClick = onAddClick) {
                Text(
                    text = "Apply",
                    style = TextStyle(
                        color = (if (isDarkTheme) TextDark else TextLight).copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}