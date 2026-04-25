package com.budgetquest.app.domain

import com.budgetquest.app.data.local.entity.UserEntity
import java.util.Calendar

object GamificationEngine {

    const val XP_PER_EXPENSE = 50
    const val XP_PER_DAILY_BUDGET_SAVED = 100
    const val XP_FOR_STREAK_7_DAYS = 200

    fun calculateLevel(xp: Int): Int {
        return (xp / 500) + 1
    }

    fun getXPForNextLevel(level: Int): Int {
        return level * 500
    }

    fun updateStreak(user: UserEntity): UserEntity {
        val lastActivity = Calendar.getInstance().apply { timeInMillis = user.lastActivityDate }
        val now = Calendar.getInstance()

        // Check if yesterday
        lastActivity.add(Calendar.DAY_OF_YEAR, 1)
        val isConsecutive = lastActivity.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                lastActivity.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        val newStreak = if (isConsecutive) user.streakCount + 1 else 1
        return user.copy(
            streakCount = newStreak,
            lastActivityDate = now.timeInMillis,
            xp = user.xp + (if (newStreak % 7 == 0) XP_FOR_STREAK_7_DAYS else 0)
        )
    }

    fun getLevelName(level: Int): String {
        return when {
            level < 5 -> "Budget Novice"
            level < 10 -> "Frugal Squire"
            level < 20 -> "Finance Knight"
            else -> "Financial Expert"
        }
    }
}
