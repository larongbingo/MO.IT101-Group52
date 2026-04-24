package org.motorph.data.loading

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRow(
    @SerialName("Employee #")
    val employeeId: String,

    @SerialName("Date")
    val date: String,

    @SerialName("Log In")
    val timeIn: String,

    @SerialName("Log Out")
    val timeOut: String,
) {
}