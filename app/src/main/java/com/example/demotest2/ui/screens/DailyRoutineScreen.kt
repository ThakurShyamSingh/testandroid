package com.example.demotest2.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import com.example.demotest2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRoutineScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf(Date()) }
    val schedule = getScheduleForDate(selectedDate)



    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) TextDark else TextLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Routine", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MainScreen") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(topBarColor)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, "dailyroutine")
        },
        content = { padding ->
            DailyRoutineContent(
                modifier = Modifier.padding(padding),
                selectedDate = selectedDate,
                onDateChange = { newDate -> selectedDate = newDate },
                schedule = schedule
            )
        }
    )
}

@Composable
fun DailyRoutineContent(
    modifier: Modifier,
    selectedDate: Date,
    onDateChange: (Date) -> Unit,
    schedule: List<Pair<String, String>>
) {
    val backgroundGradient = remember(isDarkTheme) {
        Brush.verticalGradient(
            colors = if (isDarkTheme) {
                listOf(BackgroundDark, BackgroundDarker)
            } else {
                listOf(BackgroundLight, BackgroundCardLight)
            }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Date Picker Section with Calendar
        DatePickerWithCalendar(selectedDate, onDateChange)

        // Schedule Section
        Text(
            text = "Your Schedule",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (schedule.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No classes scheduled for this day.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Color(0xFF0D47A1)
                    )
                )
            }
        } else {
            schedule.forEach { (time, activity) ->
                ScheduleItem(time, activity)
            }
        }
    }
}

@Composable
fun DatePickerWithCalendar(
    selectedDate: Date,
    onDateChange: (Date) -> Unit
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row for Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    calendar.time = selectedDate
                    calendar.add(Calendar.DATE, -1)
                    onDateChange(calendar.time)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Yesterday", color = Color.White)
            }

            Button(
                onClick = {
                    calendar.time = selectedDate
                    calendar.add(Calendar.DATE, 1)
                    onDateChange(calendar.time)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tomorrow", color = Color.White)
            }
        }

        // Selected Date Display with Calendar Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateFormat.format(selectedDate),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF0D47A1),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val currentCalendar = Calendar.getInstance()
                    currentCalendar.time = selectedDate

                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            val newCalendar = Calendar.getInstance()
                            newCalendar.set(year, month, dayOfMonth)
                            onDateChange(newCalendar.time)
                        },
                        currentCalendar.get(Calendar.YEAR),
                        currentCalendar.get(Calendar.MONTH),
                        currentCalendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Open Calendar",
                    tint = Color(0xFF0D47A1)
                )
            }
        }
    }
}

@Composable
fun ScheduleItem(time: String, activity: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
        )
        Text(
            text = activity,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        )
    }
}

fun getScheduleForDate(date: Date): List<Pair<String, String>> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = dateFormat.format(date)

    // Mock schedule data
    val schedules = mapOf(
        "2025-05-04" to listOf(
            "9:20 - 10:20" to "R Programming",
            "10:20 - 1:00" to "IDS Lab",
            "1:00 - 2:00" to "Lunch",
            "2:00 - 3:00" to "ATCD",
            "3:00 - 4:00" to "STM"
        ),
        "2025-05-05" to listOf(
            "9:00 - 10:00" to "Mathematics",
            "10:00 - 11:30" to "Physics Lab",
            "11:30 - 12:30" to "Break",
            "12:30 - 1:30" to "Computer Science",
            "2:00 - 3:00" to "Chemistry"
        )
    )

    return schedules[formattedDate] ?: emptyList()
}