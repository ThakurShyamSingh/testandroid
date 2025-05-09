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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demotest2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {

    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) Color.White else BlueTrue
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Navigate back in the stack
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
        bottomBar = { BottomNavigationBar(navController, "NewsScreen") },
        content = { padding ->
            NewsContent(
                modifier = Modifier.padding(padding)
            )
        }
    )
}

@Composable
fun NewsContent(modifier: Modifier) {
    val posters = listOf("Poster 1", "Poster 2", "Poster 3") // Add your poster data here
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
                brush = backgroundGradient,
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Latest Events",
            style = TextStyle(
                color = textColor ,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posters) { poster -> // Use the correct `items` function with a list
                NewsCard(title = poster)
            }
        }
    }
}

@Composable
fun NewsCard(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor =(if (isDarkTheme) BlueTrue else BackgroundCardLight) )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = if (isDarkTheme) TextDark else TextLight,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            )
        }
    }
}