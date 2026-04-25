package com.budgetquest.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val xp: Int = 0,
    val level: Int = 1,
    val streakCount: Int = 0,
    val lastActivityDate: Long = 0L,
    val createdAt: Long = 0L,
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 5000.0,
    val isLoggedIn: Boolean = false
)
