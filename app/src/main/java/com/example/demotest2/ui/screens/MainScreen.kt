// File: ui/screens/MainScreen.kt
package com.example.demotest2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demotest2.R
import com.example.demotest2.ui.theme.*
import androidx.compose.foundation.isSystemInDarkTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(BackgroundLight, Color.White)
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FACELYNX Dashboard",
                        style = TextStyle(color = BlueTrue,fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )

                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)

            )
        },
        bottomBar = { BottomNavigationBar(navController = navController, currentRoute = "MainScreen") }
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
                style = TextStyle(color = RedFire, fontSize = 28.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    NavigationCard("Attendance", "Check Attendance", BlueTrue) {
                        navController.navigate("AttendanceScreen")
                    }
                }
                item {
                    NavigationCard("Register Student", "Add new students", RedFire) {
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
                style = TextStyle(color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Light)
            )
        }
    }
}

@Composable
fun NavigationCard(title: String, description: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold))
            Text(description, style = TextStyle(color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp))
        }
    }
}

@Composable
fun EnhancedCard(title: String, description: String, color: Color, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, style = TextStyle(fontSize = 28.sp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, style = TextStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp))
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val selectedColor = BlueTrue
    val unselectedColor = if (isDark) Color.Gray else Color.DarkGray

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        containerColor = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        val items = listOf(
            Triple("MainScreen", R.drawable.ic_home, "Home"),            // Home / Dashboard
            Triple("NewsScreen", R.drawable.ic_newsletter, "News"),      // Newsletter
            Triple("CourseMaterialScreen", R.drawable.ic_course_material, "Materials"), // Course Material
            Triple("ProfileScreen", R.drawable.ic_profile, "Profile")    // Profile
        )

        items.forEach { (route, iconRes, description) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = description,
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
                label = null,
                alwaysShowLabel = false
            )
        }
    }
}
