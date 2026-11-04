package com.warrio.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warrio.app.auth.AuthMode
import com.warrio.app.auth.AuthUiState
import com.warrio.app.auth.AuthViewModel

@Composable
fun LoginRoute(
    onAuthenticated: () -> Unit,
    onOpenRegistration: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    AuthScreen(
        mode = AuthMode.LOGIN,
        uiState = viewModel.uiState,
        onDisplayNameChanged = viewModel::updateDisplayName,
        onEmailChanged = viewModel::updateEmail,
        onPasswordChanged = viewModel::updatePassword,
        onConfirmPasswordChanged = viewModel::updateConfirmPassword,
        onSubmit = { viewModel.submit(AuthMode.LOGIN) },
        onSwitchMode = onOpenRegistration,
    )

    LaunchedEffect(viewModel.uiState.authenticatedSession) {
        if (viewModel.uiState.authenticatedSession != null) onAuthenticated()
    }
}

@Composable
fun RegistrationRoute(
    onAuthenticated: () -> Unit,
    onOpenLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    AuthScreen(
        mode = AuthMode.REGISTER,
        uiState = viewModel.uiState,
        onDisplayNameChanged = viewModel::updateDisplayName,
        onEmailChanged = viewModel::updateEmail,
        onPasswordChanged = viewModel::updatePassword,
        onConfirmPasswordChanged = viewModel::updateConfirmPassword,
        onSubmit = { viewModel.submit(AuthMode.REGISTER) },
        onSwitchMode = onOpenLogin,
    )

    LaunchedEffect(viewModel.uiState.authenticatedSession) {
        if (viewModel.uiState.authenticatedSession != null) onAuthenticated()
    }
}

@Composable
private fun AuthScreen(
    mode: AuthMode,
    uiState: AuthUiState,
    onDisplayNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onSwitchMode: () -> Unit,
) {
    val isRegistration = mode == AuthMode.REGISTER
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (isRegistration) "Create your Warrio account" else "Welcome back",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = if (isRegistration) {
                    "Register to keep products, receipts, and warranties together."
                } else {
                    "Sign in to access your product wallet."
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )

            if (isRegistration) {
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = onDisplayNameChanged,
                    label = { Text("Full name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.displayNameError != null,
                    supportingText = uiState.displayNameError?.let { error ->
                        { Text(error) }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = { Text("Email address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.emailError != null,
                supportingText = uiState.emailError?.let { error -> { Text(error) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.passwordError != null,
                supportingText = uiState.passwordError?.let { error -> { Text(error) } },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Hide" else "Show")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isRegistration) ImeAction.Next else ImeAction.Done,
                ),
            )

            if (isRegistration) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    label = { Text("Confirm password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.confirmPasswordError != null,
                    supportingText = uiState.confirmPasswordError?.let { error ->
                        { Text(error) }
                    },
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        TextButton(onClick = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }) {
                            Text(if (confirmPasswordVisible) "Hide" else "Show")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                )
            }

            uiState.generalError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            Button(
                onClick = onSubmit,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(if (isRegistration) "Create account" else "Sign in")
                }
            }

            TextButton(
                onClick = onSwitchMode,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    if (isRegistration) {
                        "Already have an account? Sign in"
                    } else {
                        "New to Warrio? Create an account"
                    },
                )
            }

            Text(
                text = "Development mode: authentication currently uses a local fake repository.",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

