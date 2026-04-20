package org.motorph

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import org.motorph.employees.Employee

@Composable
fun MainScreen() {
    val testBarParameters: List<BarParameters> = listOf(
        BarParameters(
            dataName = "Completed",
            data = listOf(45000.6, 50000.6, 45000.0, 50000.6, 44000.0, 100000.6, 100000.0),
            barColor = Color(0xFF6C3428)
        ),
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(color = Color(0xFF1E40AF))
        ) {
            Text("Next Pay Day", color = Color.White)
            Text("4 Days Remaining", color = Color.White)

            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(16.dp)
                    .background(color = Color.Red)
            ) {
                Image(
                    Icons.Default.CalendarMonth,
                    "Calendar",
                    modifier = Modifier.fillMaxHeight(),
                    colorFilter = ColorFilter.tint(Color.White),)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Expected On", color = Color.White)
                    Text("2024-01-01", color = Color.White)
                }
            }
        }

        Column {
            Row {
                Column {
                    Text("Year to Date Summary")
                    Text("P 123,123.12")
                }
            }
            // Bar Graph meant to show relative salary per cutoff
            Box(Modifier.height(300.dp)) {
                BarChart(
                    chartParameters = testBarParameters,
                    gridColor = Color.DarkGray,
                    xAxisData = listOf(
                        "January 2026 - First Cutoff",
                        "January 2026 - Second Cutoff",
                        "February 2026 - First Cutoff",
                        "February 2026 - Second Cutoff",
                        "March 2026 - First Cutoff",
                        "March 2026 - Second Cutoff",
                        "April 2026 - First Cutoff"
                    ),
                    animateChart = true,
                    showGridWithSpacer = true,
                    yAxisStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                    ),
                    xAxisStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.W400
                    ),
                    yAxisRange = 15,
                    barWidth = 20.dp
                )
            }
        }
    }
}

data class MainUiState(
    val employee: Employee,
    val isLoading: Boolean = false,
)

class MainViewModel() : ViewModel() {

}