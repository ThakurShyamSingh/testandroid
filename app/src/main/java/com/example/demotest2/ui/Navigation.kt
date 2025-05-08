package com.example.demotest2.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.demotest2.ui.screens.*


enum class Routes(val route: String) {
    COURSE_MATERIAL("CourseMaterialScreen"),
    MAIN_SCREEN("MainScreen"),
    REGISTER_STUDENTS("RegisterStudentsScreen"),
    INNOVATIONS("InnovationsScreen"),
    DAILY_ROUTINE("DailyRoutineScreen"),
    CLASS_SELECTION("ClassSelectionScreen"),
    NEWS_SCREEN("NewsScreen"),
    PROFILE_SCREEN("ProfileScreen"),
    ATTENDANCE_SCREEN("AttendanceScreen"),
    CLUBS_SCREEN("ClubsScreen")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    NavHost(navController = navController, startDestination = Routes.MAIN_SCREEN.route) {
        composable(Routes.COURSE_MATERIAL.route) { CourseMaterialScreen(navController) }
        composable(Routes.MAIN_SCREEN.route) {MainScreen(navController) }
        composable(Routes.REGISTER_STUDENTS.route) { RegisterStudentsScreen(navController) }
        composable(Routes.INNOVATIONS.route){ InnovationsScreen(navController)}
        composable(Routes.DAILY_ROUTINE.route){ DailyRoutineScreen(navController) }
        composable(Routes.CLASS_SELECTION.route){ ClassSelectionScreen(navController) }
        composable(Routes.NEWS_SCREEN.route) { NewsScreen(navController) }
        composable(Routes.PROFILE_SCREEN.route){ ProfileScreen(navController) }
        composable(Routes.ATTENDANCE_SCREEN.route){ AttendanceScreen(navController) }
        composable(Routes.CLUBS_SCREEN.route){ ClubsScreen(navController) }

    }
}
