package org.motorph.employee.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.motorph.core.results.Failure
import org.motorph.core.results.Success
import org.motorph.employees.Employee
import org.motorph.employees.EmployeeRepository
import org.motorph.employees.login.LoginService

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    login: (onSuccess: (Employee) -> Unit) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        ) {
        Text(
            text = "Login",
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChanged,
            label = { Text("Username") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { login { } },
            enabled = !uiState.isLoading,
        ) {
            Text(text = if (uiState.isLoading) "Logging in..." else "Login")
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

class LoginViewModel(
    private val loginService: LoginService
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login(onSuccess: (Employee) -> Unit) {
        val state = uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            val result = loginService.login(state.username, state.password)

            if (result is Success<Employee>) {
                _uiState.value = state.copy(isLoading = false)
                onSuccess(result.value)
            } else {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = (result as Failure<Employee>).exception.message
                )
            }
        }
    }
}