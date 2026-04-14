package org.motorph.employee.login

import androidx.room.Entity
import androidx.room.ForeignKey
import org.motorph.employee.EmployeeEntity
import org.motorph.employees.login.Login

@Entity(
    tableName = "logins",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["EmployeeId"],
            childColumns = ["EmployeeId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
data class LoginEntity(
    val employeeId: String,
    val username: String,
    val password: String
) {
    fun toLogin() = Login(employeeId, username, password)

    companion object {
        fun fromLogin(login: Login) =
            LoginEntity(login.EmployeeId, login.Username, login.Password)
    }
}
