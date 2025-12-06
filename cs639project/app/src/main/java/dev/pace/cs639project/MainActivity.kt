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
                MomentumApp()

                // Commented-out test screens below for clarity
                // AddHabitScreen()
                // FirestoreTestScreen()
                // SettingsScreen()      // test screen -Siming
                // ApiSuggestionsScreen() // test screen -Siming
            }

        }
    }
}