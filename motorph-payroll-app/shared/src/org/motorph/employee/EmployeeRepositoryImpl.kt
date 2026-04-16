package org.motorph.employee

import androidx.room.*
import kotlinx.coroutines.runBlocking
import org.motorph.core.MotorPhException
import org.motorph.core.results.Result
import org.motorph.employees.Employee
import org.motorph.employees.EmployeeRepository

class EmployeeRepositoryImpl(private val employeeDao: EmployeeDao) : EmployeeRepository {
    override fun addEmployee(newEmployee: Employee): Result<Employee> {
        val employeeEntity = EmployeeEntity.fromEmployee(newEmployee)
        val result = runCatching { runBlocking { employeeDao.insert(employeeEntity) } }
        return result.fold(
            onSuccess = {
                if (it > 0) {
                    Result.success(newEmployee)
                } else {
                    Result.failure<Employee>(MotorPhException("Failed to add employee"))
                }
            },
            onFailure = { Result.failure<Employee>(MotorPhException("Failed to add employee", it)) }
        )
    }

    override fun updateEmployee(updatedEmployee: Employee): Result<Employee> {
        val employeeEntity = EmployeeEntity.fromEmployee(updatedEmployee)
        val result = runCatching { runBlocking { employeeDao.update(employeeEntity) } }
        return result.fold(
            onSuccess = {
                if (it > 0) {
                    Result.success(updatedEmployee)
                } else {
                    Result.failure<Employee>(MotorPhException("Failed to update employee"))
                }
            },
            onFailure = { Result.failure<Employee>(MotorPhException("Failed to update employee", it)) }
        )
    }

    override fun getAllEmployees(): List<Employee> {
        val result = runCatching { runBlocking { employeeDao.getAllEmployees() } }
        return result.fold(
            onSuccess = { value -> value.map { it.toEmployee() } },
            onFailure = { emptyList() }
        )
    }

    override fun getEmployeeByEmployeeId(employeeId: String): Employee? {
        val result = runCatching { runBlocking { employeeDao.getEmployeeByEmployeeId(employeeId) } }
        return result.fold(
            onSuccess = { value -> value?.toEmployee() },
            onFailure = { null }
        )
    }
}

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees WHERE employeeId = :employeeId LIMIT 1")
    suspend fun getEmployeeByEmployeeId(employeeId: String): EmployeeEntity?

    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<EmployeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity): Int
}