package com.budgetquest.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val date: Long, // Epoch timestamp
    val startTime: Long,
    val endTime: Long,
    val description: String,
    val categoryId: Int,
    val userId: Int = 0,
    val imagePath: String? = null
)
