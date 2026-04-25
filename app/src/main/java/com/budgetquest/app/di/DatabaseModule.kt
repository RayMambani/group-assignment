package com.budgetquest.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.budgetquest.app.data.local.BudgetDatabase
import com.budgetquest.app.data.local.dao.BudgetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            safeAddColumn(db, "expenses", "startTime", "INTEGER NOT NULL DEFAULT 0")
            safeAddColumn(db, "expenses", "endTime", "INTEGER NOT NULL DEFAULT 0")
            
            safeAddColumn(db, "users", "xp", "INTEGER NOT NULL DEFAULT 0")
            safeAddColumn(db, "users", "level", "INTEGER NOT NULL DEFAULT 1")
            safeAddColumn(db, "users", "streakCount", "INTEGER NOT NULL DEFAULT 0")
            safeAddColumn(db, "users", "lastActivityDate", "INTEGER NOT NULL DEFAULT 0")
        }
    }

    private fun safeAddColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String, columnDef: String) {
        try {
            val cursor = db.query("PRAGMA table_info($tableName)")
            var columnExists = false
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                if (name == columnName) {
                    columnExists = true
                    break
                }
            }
            cursor.close()

            if (!columnExists) {
                db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnDef")
            }
        } catch (e: Exception) {
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            safeAddColumn(db, "users", "minMonthlyGoal", "REAL NOT NULL DEFAULT 0.0")
            safeAddColumn(db, "users", "maxMonthlyGoal", "REAL NOT NULL DEFAULT 5000.0")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            safeAddColumn(db, "users", "createdAt", "INTEGER NOT NULL DEFAULT 0")
            db.execSQL("CREATE TABLE IF NOT EXISTS `quests` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `progress` INTEGER NOT NULL, `color` TEXT NOT NULL)")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            safeAddColumn(db, "expenses", "userId", "INTEGER NOT NULL DEFAULT 0")
            safeAddColumn(db, "categories", "userId", "INTEGER NOT NULL DEFAULT 0")
            safeAddColumn(db, "quests", "userId", "INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BudgetDatabase {
        return Room.databaseBuilder(
            context,
            BudgetDatabase::class.java,
            "budget_quest.db"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideBudgetDao(database: BudgetDatabase): BudgetDao {
        return database.budgetDao()
    }
}
