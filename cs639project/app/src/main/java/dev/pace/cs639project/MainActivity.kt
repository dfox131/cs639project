package dev.pace.cs639project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.pace.cs639project.ui.theme.Cs639projectTheme
import androidx.compose.material3.MaterialTheme
import dev.pace.cs639project.screens.AddHabitScreen
import dev.pace.cs639project.screens.FirestoreTestScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MaterialTheme {
                AddHabitScreen()
                // MomentumApp()
                // FirestoreTestScreen()
                // SettingsScreen()      // test screen -Siming
                // ApiSuggestionsScreen() // test screen -Siming
            }

        }
    }
}
//@Composable
//fun DailyHubScreen(
//    habits: List<Habit>
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Daily Hub") })
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { /* Navigate to Rewards Screen */ },
//                content = { Icon(Icons.Filled.Trophy, contentDescription = "Rewards") }
//            )
//        },
//        modifier = Modifier.fillMaxSize()
//    ) { paddingValues ->
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            item {
//                Text(text = " 2 Days Streak", modifier = Modifier.padding(16.dp))
//            }
//
//            items(habits) { habit ->
//                HabitCompletionCard(habit = habit)
//            }
//        }
//    }
//}