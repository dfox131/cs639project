package dev.pace.cs639project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MaterialTheme {

                //HabitListScreen()
                   //AddHabitScreen()
                  MomentumApp()
                // FirestoreTestScreen()
                 //SettingsScreen()      // test screen -Siming
                // ApiSuggestionsScreen() // test screen -Siming
            }

        }
    }
}