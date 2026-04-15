package org.motorph.employee

import org.koin.dsl.module
import org.motorph.data.AppDatabase
import org.motorph.employee.login.loginModule
import org.motorph.employees.EmployeeRepository
import org.motorph.employees.ManageEmployeesService
import org.motorph.employees.ManageEmployeesServiceImpl

val employeeModule = module {
    single<EmployeeDao> { get<AppDatabase>().employeeDao() }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }
    single<ManageEmployeesService> { ManageEmployeesServiceImpl(get()) }
    includes(loginModule)
}