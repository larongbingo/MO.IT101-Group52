package org.motorph.employee.login

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.runBlocking
import org.motorph.core.MotorPhException
import org.motorph.core.results.Result
import org.motorph.employees.Employee
import org.motorph.employees.login.Login
import org.motorph.employees.login.LoginRepository

class LoginRepositoryImpl(private val loginDao: LoginDao) : LoginRepository {
    override fun addLogin(login: Login): Result<Login> {
        val loginEntity = LoginEntity.fromLogin(login)
        val result = runCatching { runBlocking { loginDao.insert(loginEntity) } }
        return result.fold(
            onSuccess = { Result.success(login) },
            onFailure = { Result.failure(MotorPhException("Failed to add login", it)) }
        )
    }

    override fun updateLogin(updatedLogin: Login): Result<Login> {
        val loginEntity = LoginEntity.fromLogin(updatedLogin)
        val result = runCatching { runBlocking { loginDao.update(loginEntity) } }
        return result.fold(
            onSuccess = { Result.success(updatedLogin) },
            onFailure = { Result.failure(MotorPhException("Failed to update login", it)) }
        )
    }

    override fun getLoginByUsername(username: String): Login? {
        val result = runCatching { runBlocking { loginDao.getLoginByUsername(username) } }
        return result.fold(
            onSuccess = { value -> value?.toLogin() },
            onFailure = { null }
        )
    }
}

@Dao
interface LoginDao {
    @Query("SELECT * FROM logins WHERE username = :username")
    suspend fun getLoginByUsername(username: String): LoginEntity?

    @Insert
    suspend fun insert(login: LoginEntity): Long

    @Update
    suspend fun update(login: LoginEntity): Int
}