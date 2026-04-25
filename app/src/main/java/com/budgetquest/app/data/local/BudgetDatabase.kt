package com.budgetquest.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.budgetquest.app.data.local.dao.BudgetDao
import com.budgetquest.app.data.local.entity.CategoryEntity
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.data.local.entity.QuestEntity
import com.budgetquest.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, CategoryEntity::class, ExpenseEntity::class, QuestEntity::class],
    version = 7,
    exportSchema = false
)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
}
