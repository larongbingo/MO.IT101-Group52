package org.motorph.ui.mainscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters

@Composable
fun PayrollBarGraph() {
    val testBarParameters: List<BarParameters> = listOf(
        BarParameters(
            dataName = "Completed",
            data = listOf(45000.6, 50000.6, 45000.0, 50000.6, 44000.0, 100000.6, 100000.0),
            barColor = Color(0xFF6C3428)
        ),
    )

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