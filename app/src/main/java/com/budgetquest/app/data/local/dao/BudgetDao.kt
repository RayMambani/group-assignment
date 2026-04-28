package com.budgetquest.app.data.local.dao

import androidx.room.*
import com.budgetquest.app.data.local.entity.CategoryEntity
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.data.local.entity.QuestEntity
import com.budgetquest.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun clearLoginStatus()

    @Query("SELECT * FROM categories WHERE userId = :userId")
    fun getAllCategories(userId: Int): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM expenses WHERE categoryId = :categoryId")
    suspend fun deleteExpensesByCategory(categoryId: Int)

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpenses(userId: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesInRange(userId: Int, startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getTotalSpendingInRange(userId: Int, startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    fun getSpendingByCategory(userId: Int, startDate: Long, endDate: Long): Flow<List<CategorySpending>>

    @Query("SELECT * FROM quests WHERE userId = :userId")
    fun getAllQuests(userId: Int): Flow<List<QuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity): Long

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Delete
    suspend fun deleteQuest(quest: QuestEntity)
}

data class CategorySpending(
    val categoryId: Int,
    val total: Double
)
