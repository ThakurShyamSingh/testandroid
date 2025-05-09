package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.demotest2.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
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
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                    .background(topBarColor),
                title = {
                    Text(
                        "FACELYNX Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(color = textColor)
                    )
                },
                actions = {
                    IconToggleButton(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = !isDarkTheme }
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                            contentDescription = "Toggle Theme",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = "MainScreen")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Welcome to FACELYNX",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = if (isDarkTheme) RedAccentDark else RedAccent
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    NavigationCard("Attendance", "Check Attendance", BlueTrue) {
                        navController.navigate("AttendanceScreen")
                    }
                }
                item {
                    NavigationCard("Register Student", "Add new students", RedAccent) {
                        navController.navigate("RegisterStudentsScreen")
                    }
                }
                item {
                    NavigationCard("Time Table", "View schedule", CreamSoft) {
                        navController.navigate("DailyRoutineScreen")
                    }
                }
            }

            EnhancedCard("Innovations", "Explore new tech", Color(0xFF673AB7), "🚀") {
                navController.navigate("InnovationsScreen")
            }
            EnhancedCard("Clubs", "Join student clubs", Color(0xFF009688), "🎨") {
                navController.navigate("ClubsScreen")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Powered by FACELYNX",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
    }
}

@Composable
fun NavigationCard(title: String, description: String, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(300.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f))
            )
        }
    }
}

@Composable
fun EnhancedCard(title: String, description: String, color: Color, emoji: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String) {
    val backgroundColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val selectedColor = BlueTrue
    val unselectedColor = if (isDarkTheme) Color.Gray else Color.DarkGray

    NavigationBar(
        modifier = Modifier
            .shadow(4.dp)
            .fillMaxWidth()
            .height(56.dp),
        containerColor = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        val items = listOf(
            Triple("MainScreen", Icons.Filled.Home, "Home"),
            Triple("NewsScreen", Icons.AutoMirrored.Filled.Article, "News"),
            Triple("ClassSelectionScreen", Icons.Filled.School, "Materials"),
            Triple("ProfileScreen", Icons.Filled.Person, "Profile")
        )

        items.forEach { (route, icon, description) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        modifier = Modifier.size(24.dp),
                        tint = if (currentRoute == route) selectedColor else unselectedColor
                    )
                },
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}
