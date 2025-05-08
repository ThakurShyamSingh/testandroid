    package com.example.demotest2.ui.screens

    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Edit
    import androidx.compose.material.icons.filled.Face
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Visibility
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(navController: NavController) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Main Menu", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EA))
                )
            },
            bottomBar = { BottomNavigationBar(navController, "MainScreen") },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Welcome Title
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Welcome to FACELYNX",
                        style = TextStyle(
                            color = Color(0xFF4A148C),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Horizontal Navigation Cards
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            NavigationCard(
                                title = "Attendance",
                                description = "Check Attendance",
                                backgroundColor = Color(0xFFB39DDB),
                                onClick = { navController.navigate("AttendanceScreen") }
                            )
                        }
                        item {
                            NavigationCard(
                                title = "Register Student",
                                description = "Add new students to the system",
                                backgroundColor = Color(0xFF9575CD),
                                onClick = { navController.navigate("RegisterStudentsScreen") }
                            )
                        }
                        item {
                            NavigationCard(
                                title = "Time Table",
                                description = "Check Time Table",
                                backgroundColor = Color(0xFF7E57C2),
                                onClick = { navController.navigate("DailyRoutineScreen") }
                            )
                        }
                    }

                    // Enhanced Cards for Innovations and Clubs
                    EnhancedCard(
                        title = "Innovations",
                        description = "Explore the latest innovations by FACELYNX.",
                        backgroundColor = Color(0xFFFFC107),
                        emoji = "🚀",
                        onClick = { navController.navigate("InnovationsScreen") }
                    )
                    EnhancedCard(
                        title = "Clubs",
                        description = "Discover Clubs in our College.",
                        backgroundColor = Color(0xFF4DB6AC),
                        emoji = "🎨",
                        onClick = { navController.navigate("ClubsScreen") }
                    )

                    // Footer Text
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Powered by FACELYNX",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        )
    }

    @Composable
    fun EnhancedCard(
        title: String,
        description: String,
        backgroundColor: Color,
        emoji: String,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon or Emoji
                Box(
                    modifier = Modifier
                        .size(64.dp)
    //                    .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        style = TextStyle(fontSize = 28.sp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                // Text Content
                Column {
                    Text(
                        text = title,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun NavigationCard(
        title: String,
        description: String,
        backgroundColor: Color,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
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



    @Composable
    fun BottomNavigationBar(navController: NavController, name: String) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(64.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8)) // Subtle gradient for premium feel
                    ),
                    shape = RoundedCornerShape(24.dp) // Rounded shape for a modern look
                )
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            containerColor = Color.Transparent,
            contentColor = Color.Gray
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_home), // Use your custom drawable resource
                        contentDescription = "Home",
                        tint = if (name == "MainScreen") Color(0xFF388E3C) else Color.Gray
                    )
                },
                label = {
                    Text(
                        "",
                        color = if (name == "MainScreen") Color(0xFF388E3C) else Color.Gray,
                        fontSize = 12.sp
                    )
                },
                selected = name == "MainScreen",
                onClick = {
                    if (name != "MainScreen") {
                        navController.navigate("MainScreen")
                    }
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_newsletter), // Use your custom drawable resource
                        contentDescription = "NewsScreen",
                        tint = if (name == "NewsScreen") Color(0xFF388E3C) else Color.Gray
                    )
                },
                label = {
                    Text(
                        "",
                        color = if (name == "NewsScreen") Color(0xFF445744) else Color.Gray,
                        fontSize = 12.sp
                    )
                },
                selected = name == "NewsScreen",
                onClick = {
                    if (name != "NewsScreen") {
                        navController.navigate("NewsScreen")
                    }
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_course_material), // Use your custom drawable resource
                        contentDescription = "CourseMaterial",
                        tint = if (name == "CourseMaterial") Color(0xFF388E3C) else Color.Gray
                    )
                },
                label = {
                    Text(
                        "",
                        color = if (name == "ClassSelectionScreen") Color(0xFF388E3C) else Color.Gray,
                        fontSize = 12.sp
                    )
                },
                selected = name == "CourseMaterialScreen",
                onClick = {
                    if (name != "CourseMaterialScreen") {
                        navController.navigate("ClassSelectionScreen")
                    }
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_profile), // Use your custom drawable resource
                        contentDescription = "ProfileScreen",
                        tint = if (name == "ProfileScreen") Color(0xFF388E3C) else Color.Gray
                    )
                },
                label = {
                    Text(
                        "",
                        color = if (name == "ProfileScreen") Color(0xFF388E3C) else Color.Gray,
                        fontSize = 12.sp
                    )
                },
                selected = name == "ProfileScreen",
                onClick = {
                    if (name != "ProfileScreen") {
                        navController.navigate("ProfileScreen")
                    }
                }
            )
        }
    }