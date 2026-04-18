package org.motorph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import org.motorph.employee.login.LoginScreen
import org.motorph.employee.login.LoginViewModel

sealed class AppRoute(val path: String) {
    data object Login : AppRoute("/login")
    data object Home : AppRoute("/home")
}

@Composable
fun AppRouter() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoute.Login.path) {
        composable(AppRoute.Login.path) {
            val viewModel: LoginViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onUsernameChanged = viewModel::onUsernameChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                login = {
                    viewModel.login {
                        navController.navigate(AppRoute.Home.path)
                    }
                }
            )
        }

        composable(AppRoute.Home.path) {
            MainShell()
        }
    }
}