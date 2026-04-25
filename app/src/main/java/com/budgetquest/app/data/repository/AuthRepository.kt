package com.budgetquest.app.data.repository

import com.budgetquest.app.data.local.dao.BudgetDao
import com.budgetquest.app.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    suspend fun register(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val existingUser = budgetDao.getUserByUsername(username)
        if (existingUser != null) return@withContext false

        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        val newUser = UserEntity(
            username = username, 
            passwordHash = passwordHash,
            createdAt = System.currentTimeMillis()
        )
        budgetDao.insertUser(newUser)
        true
    }

    suspend fun login(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val user = budgetDao.getUserByUsername(username) ?: return@withContext false
        if (BCrypt.checkpw(password, user.passwordHash)) {
            budgetDao.clearLoginStatus()
            budgetDao.updateUser(user.copy(isLoggedIn = true))
            true
        } else {
            false
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        budgetDao.clearLoginStatus()
    }

    fun getCurrentUser() = budgetDao.getUser()

    suspend fun updateXP() {
    }

    suspend fun updateUser(user: UserEntity) {
        budgetDao.updateUser(user)
    }
}
