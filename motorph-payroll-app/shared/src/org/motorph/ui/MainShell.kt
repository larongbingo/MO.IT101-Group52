package org.motorph.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Destination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    HOME("home", Icons.Default.Home, "Home"),
}

@Composable
fun MainShell() {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination.ordinal) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedDestination == 0,
                    onClick = {
                        navController.navigate(Destination.HOME.route)
                        selectedDestination = 0
                    },
                    icon = {
                        Icon(Icons.Default.Home, "Home")
                    },
                    label = {
                        Text(Destination.HOME.label)
                    }
                )
            }
        }
    ) {
        AppNavHost(navController, startDestination)
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
) {
    NavHost(navController = navController, startDestination = startDestination.route) {
        composable(Destination.HOME.route) {
            MainScreen()
        }
    }
}