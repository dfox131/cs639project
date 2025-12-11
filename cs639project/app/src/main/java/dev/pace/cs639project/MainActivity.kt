package dev.pace.cs639project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // automatically logs user out when reloading the app
        FirebaseAuth.getInstance().signOut()

        setContent {
            MaterialTheme {
                // 🔥 AppNavigation now handles login/signup + MomentumApp
                AppNavigation()
            }
        }
    }
}
