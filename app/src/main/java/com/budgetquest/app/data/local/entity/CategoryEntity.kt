package com.budgetquest.app.data.local.entity

import androidx.room.*

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "userId"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String, // String resource name or icon identifier
    val monthlyLimit: Double,
    val userId: Int = 0
)
