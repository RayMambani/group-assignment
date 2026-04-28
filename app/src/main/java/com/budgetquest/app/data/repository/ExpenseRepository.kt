package com.budgetquest.app.data.repository

import com.budgetquest.app.data.local.dao.BudgetDao
import com.budgetquest.app.data.local.entity.CategoryEntity
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.data.local.entity.QuestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    fun getAllExpenses(userId: Int) = budgetDao.getAllExpenses(userId)

    fun getExpensesInRange(userId: Int, start: Long, end: Long) = budgetDao.getExpensesInRange(userId, start, end)

    suspend fun insertExpense(expense: ExpenseEntity) = budgetDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntity) = budgetDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseEntity) = budgetDao.deleteExpense(expense)

    fun getAllCategories(userId: Int) = budgetDao.getAllCategories(userId)

    suspend fun insertCategory(category: CategoryEntity) = budgetDao.insertCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = budgetDao.deleteCategory(category)

    suspend fun deleteExpensesByCategory(categoryId: Int) = budgetDao.deleteExpensesByCategory(categoryId)


    fun getTotalSpending(userId: Int, start: Long, end: Long) = budgetDao.getTotalSpendingInRange(userId, start, end)

    fun getSpendingByCategory(userId: Int, start: Long, end: Long) = budgetDao.getSpendingByCategory(userId, start, end)

    fun getAllQuests(userId: Int) = budgetDao.getAllQuests(userId)

    suspend fun insertQuest(quest: QuestEntity) = budgetDao.insertQuest(quest)

    suspend fun updateQuest(quest: QuestEntity) = budgetDao.updateQuest(quest)

    suspend fun deleteQuest(quest: QuestEntity) = budgetDao.deleteQuest(quest)
}
