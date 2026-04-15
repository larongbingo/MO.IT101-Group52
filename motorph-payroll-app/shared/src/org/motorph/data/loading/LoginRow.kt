package org.motorph.data.loading

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.motorph.employees.login.Login

@Serializable
data class LoginRow(
    @SerialName("Username")
    val username: String,

    @SerialName("Password")
    val password: String,

    @SerialName("Employee #")
    val employeeId: String,
) {

    fun toLogin(): Login {
        return Login(username, password, employeeId)
    }

}