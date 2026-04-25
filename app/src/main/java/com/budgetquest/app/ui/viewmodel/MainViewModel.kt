package com.budgetquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetquest.app.data.local.entity.CategoryEntity
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.data.local.entity.QuestEntity
import com.budgetquest.app.data.local.entity.UserEntity
import com.budgetquest.app.data.repository.ExpenseRepository
import com.budgetquest.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.getCurrentUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val allExpenses = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else expenseRepository.getAllExpenses(user.id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val categories = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else expenseRepository.getAllCategories(user.id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val allQuests = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else expenseRepository.getAllQuests(user.id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _startDateFilter = MutableStateFlow<Long>(
        Calendar.getInstance().apply { 
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    )
    val startDateFilter = _startDateFilter.asStateFlow()

    private val _endDateFilter = MutableStateFlow<Long>(System.currentTimeMillis())
    val endDateFilter = _endDateFilter.asStateFlow()

    val categorySpending = combine(currentUser, _startDateFilter, _endDateFilter) { user, start, end ->
        Triple(user, start, end)
    }.flatMapLatest { (user, start, end) ->
        if (user == null) flowOf(emptyList())
        else expenseRepository.getSpendingByCategory(user.id, start, end)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val monthlySpending = combine(currentUser, _startDateFilter, _endDateFilter) { user, start, end ->
        Triple(user, start, end)
    }.flatMapLatest { (user, start, end) ->
        if (user == null) flowOf(0.0)
        else expenseRepository.getTotalSpending(user.id, start, end).map { it ?: 0.0 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val filteredExpenses = combine(currentUser, _startDateFilter, _endDateFilter) { user, start, end ->
        Triple(user, start, end)
    }.flatMapLatest { (user, start, end) ->
        if (user == null) flowOf(emptyList())
        else expenseRepository.getExpensesInRange(user.id, start, end)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredTotal = filteredExpenses.map { list ->
        list.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    data class ChartBarData(val name: String, val value: Float, val color: String)

    val chartData = combine(categorySpending, categories) { spendingList, catList ->
        if (spendingList.isEmpty()) {
            emptyList<ChartBarData>()
        } else {
            val maxTotal = spendingList.maxOfOrNull { it.total } ?: 1.0
            spendingList
                .sortedByDescending { it.total }
                .take(5)
                .map { spending ->
                    val catName = catList.find { it.id == spending.categoryId }?.name ?: "???"
                    val color = when(catName.lowercase()) {
                        "groceries" -> "NeonCyan"
                        "transport" -> "NeonPurple"
                        "bills" -> "NeonPink"
                        "entertainment" -> "NeonGreen"
                        else -> "CyberGold"
                    }
                    ChartBarData(
                        name = catName,
                        value = (spending.total / maxTotal).toFloat().coerceIn(0.1f, 1f),
                        color = color
                    )
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        currentUser.onEach { user ->
            if (user != null) {
                seedInitialCategories(user)
            }
        }.launchIn(viewModelScope)
    }

    private var hasAttemptedSeed = false

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    private fun seedInitialCategories(user: UserEntity) {
        if (hasAttemptedSeed) return
        hasAttemptedSeed = true
        
        viewModelScope.launch {
            if (user.createdAt == 0L) {
                authRepository.updateUser(user.copy(createdAt = System.currentTimeMillis()))
            }

            val currentCategories = expenseRepository.getAllCategories(user.id).first()
            if (currentCategories.isEmpty()) {
                val initial = listOf(
                    CategoryEntity(name = "Groceries", icon = "shopping_cart", monthlyLimit = 2000.0, userId = user.id),
                    CategoryEntity(name = "Transport", icon = "directions_car", monthlyLimit = 1500.0, userId = user.id),
                    CategoryEntity(name = "Entertainment", icon = "movie", monthlyLimit = 800.0, userId = user.id),
                    CategoryEntity(name = "Bills", icon = "receipt_long", monthlyLimit = 1200.0, userId = user.id)
                )
                initial.forEach { category ->
                    expenseRepository.insertCategory(category)
                }
            } else {
                currentCategories.find { it.name.lowercase() == "savings" }?.let {
                    expenseRepository.deleteCategory(it)
                }
            }

            val currentQuests = expenseRepository.getAllQuests(user.id).first()
            if (currentQuests.isEmpty()) {
                val initialQuests = listOf(
                    QuestEntity(title = "SAVE R500 ON ENTERTAINMENT", description = "Keep entertainment spending below R300 this week.", progress = 40, color = "NeonPurple", userId = user.id),
                    QuestEntity(title = "STREAK MASTER", description = "Log expenses for 5 days straight.", progress = 80, color = "NeonCyan", userId = user.id),
                    QuestEntity(title = "BUDGET ARCHIVE", description = "Stay under 90% of total budget this month.", progress = 15, color = "NeonGreen", userId = user.id)
                )
                for (quest in initialQuests) {
                    expenseRepository.insertQuest(quest)
                }
            }
        }
    }

    fun updateUserGoals(min: Double, max: Double) {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                val updatedUser = user.copy(
                    minMonthlyGoal = min,
                    maxMonthlyGoal = max
                )
                authRepository.updateUser(updatedUser)
            }
        }
    }

    fun addExpenseWithXP(expense: ExpenseEntity) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            expenseRepository.insertExpense(expense.copy(userId = user.id))
            _endDateFilter.value = System.currentTimeMillis() // Auto-refresh range to include new expense
            _events.emit("EXPENSE_ADDED")
            
            val currentTimestamp = System.currentTimeMillis()
            
            val lastActivity = Calendar.getInstance().apply { timeInMillis = user.lastActivityDate }
            val currentDay = Calendar.getInstance().apply { timeInMillis = currentTimestamp }
            
            val isSameDay = lastActivity.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR) &&
                            lastActivity.get(Calendar.DAY_OF_YEAR) == currentDay.get(Calendar.DAY_OF_YEAR)
            
            val isYesterday = lastActivity.apply { add(Calendar.DAY_OF_YEAR, 1) }
                                .let { it.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR) &&
                                        it.get(Calendar.DAY_OF_YEAR) == currentDay.get(Calendar.DAY_OF_YEAR) }

            val newStreak = when {
                isSameDay -> user.streakCount
                isYesterday -> user.streakCount + 1
                else -> 1
            }

            val newXp = user.xp + 50
            val newLevel = (newXp / 1000) + 1
            
            val updatedUser = user.copy(
                xp = newXp,
                level = newLevel,
                streakCount = newStreak,
                lastActivityDate = currentTimestamp
            )
            
            authRepository.updateUser(updatedUser)
        }
    }

    fun addCategory(name: String, limit: Double, icon: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            expenseRepository.insertCategory(
                CategoryEntity(
                    name = name,
                    monthlyLimit = limit,
                    icon = icon,
                    userId = user.id
                )
            )
        }
    }

    fun setDateFilters(start: Long, end: Long) {
        _startDateFilter.value = start
        _endDateFilter.value = end
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
