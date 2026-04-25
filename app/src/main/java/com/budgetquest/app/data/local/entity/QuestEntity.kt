package com.budgetquest.app.data.local.entity

import androidx.room.*

@Entity(
    tableName = "quests",
    indices = [Index(value = ["title", "userId"], unique = true)]
)
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val progress: Int,
    val color: String,
    val userId: Int = 0
)
