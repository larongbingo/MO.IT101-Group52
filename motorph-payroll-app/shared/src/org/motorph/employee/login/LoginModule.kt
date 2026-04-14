package org.motorph.employee.login

import org.koin.dsl.module
import org.motorph.data.AppDatabase
import org.motorph.employees.crypto.StringHashing
import org.motorph.employees.login.LoginRepository
import org.motorph.employees.login.LoginService
import org.motorph.employees.login.LoginServiceImpl

val loginModule = module {
    single<LoginDao> { get<AppDatabase>().loginDao() }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    single<StringHashing> { BcryptStringHashing() }
    single<LoginService> { LoginServiceImpl(get(), get(), get()) }
}