package org.motorph.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NextPayrollCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color(0xFF173FBB),
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(30.dp)) {
            Text(
                text = "NEXT PAY DAY",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "04",
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 46.sp
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Days Remaining",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = Color.White.copy(alpha = 0.12f),
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.85f))
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Column {
                        Text(
                            text = "EXPECTED ON",
                            color = Color.White.copy(alpha = 0.62f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp
                        )

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = "Friday, Oct 27, 2023",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}