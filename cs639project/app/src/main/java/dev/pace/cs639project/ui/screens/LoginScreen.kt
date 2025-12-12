package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.viewmodel.AuthViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import dev.pace.cs639project.ui.components.SavedConfirmationMessage


@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onGoToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val authViewModel: AuthViewModel = viewModel()
    val logoutSuccess by authViewModel.logoutSuccess.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (logoutSuccess) {
            SavedConfirmationMessage(message = "Logged out successfully")
        }

        LaunchedEffect(logoutSuccess) {
            if (logoutSuccess) {
                kotlinx.coroutines.delay(1500)
                authViewModel.clearLogoutSuccess()
            }
        }


        Text("Login", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            }
        )

        Button(
            onClick = {
                viewModel.login(email, password) {
                    onLoginSuccess()
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Logging in…" else "Login")
        }

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        TextButton(onClick = onGoToSignup) {
            Text("Don’t have an account? Sign up")
        }
    }
}
