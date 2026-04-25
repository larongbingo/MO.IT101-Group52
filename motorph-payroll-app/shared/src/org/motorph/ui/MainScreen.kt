package org.motorph.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import org.motorph.employees.Employee
import org.motorph.ui.mainscreen.NextPayrollCard
import org.motorph.ui.mainscreen.PayrollBarGraph

@Composable
fun MainScreen() {
    Column(Modifier.padding(8.dp)) {
        NextPayrollCard()

        PayrollBarGraph()
    }
}

data class MainUiState(
    val employee: Employee,
    val isLoading: Boolean = false,
)

class MainViewModel() : ViewModel() {

}