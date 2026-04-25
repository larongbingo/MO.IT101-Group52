package org.motorph

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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

    Column(Modifier.padding(8.dp)) {
        Card(shape = RoundedCornerShape(8.dp), backgroundColor = Color(0xFF1E40AF)) {
            Column(Modifier.padding(16.dp)) {
                Text("Next Pay Day", color = Color.White)
                Text("4 Days Remaining", color = Color.White)

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color(0xFF0EA5E9)
                ) {
                    Row(Modifier.padding(8.dp)) {
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
            }
        }

        Column {
            Row {
                Column {
                    Text("Year to Date Summary")
                    Text("P 123,123.12")
                }
            }
            Box(Modifier.height(300.dp).padding(8.dp)) {
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