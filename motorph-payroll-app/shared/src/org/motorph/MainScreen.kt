package org.motorph

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.motorph.employees.Employee

@Composable
fun MainScreen() {
    Column {
        Column {
            Text("Next Pay Day")
            Text("4 Days Remaining")

            Row {
                // Image(null) // Calendar Logo
                Text("Expected On")
                Text("2024-01-01")
            }
        }

        Column {
            Row {
                Column {
                    Text("Year to Date Summary")
                    Text("P 123,123.12")
                }
            }
            // Bar Graph of each cutoff, 8 bars
        }
    }
}

data class MainUiState(
    val employee: Employee,
    val isLoading: Boolean = false,
)

class MainViewModel() : ViewModel() {

}