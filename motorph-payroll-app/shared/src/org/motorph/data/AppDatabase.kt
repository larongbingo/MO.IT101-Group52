package org.motorph.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import org.motorph.employee.EmployeeDao
import org.motorph.employee.EmployeeEntity
import org.motorph.employee.login.LoginDao
import org.motorph.employee.login.LoginEntity

@Database(
    entities = [
        EmployeeEntity::class,
        LoginEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(MotorPhTypeConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun loginDao(): LoginDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
    builder.setDriver(BundledSQLiteDriver()).build()

val appDatabaseModule = module {
    includes(platformModule)
    single { getRoomDatabase(get()) }
}

expect val platformModule: Module