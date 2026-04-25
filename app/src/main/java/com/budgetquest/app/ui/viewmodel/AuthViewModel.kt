package com.budgetquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetquest.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _authEvent = MutableSharedFlow<AuthResult>()
    val authEvent = _authEvent.asSharedFlow()

    fun onUsernameChange(value: String) { _username.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login() {
        viewModelScope.launch {
            val cleanUsername = _username.value.trim()
            val cleanPassword = _password.value.trim()
            
            if (cleanUsername.isEmpty() || cleanPassword.isEmpty()) {
                _authEvent.emit(AuthResult.Error("Please enter both username and password"))
                return@launch
            }

            _isLoading.value = true
            val success = repository.login(cleanUsername, cleanPassword)
            _isLoading.value = false
            if (success) _authEvent.emit(AuthResult.Success)
            else _authEvent.emit(AuthResult.Error("Invalid credentials"))
        }
    }

    fun register() {
        viewModelScope.launch {
            val cleanUsername = _username.value.trim()
            val cleanPassword = _password.value.trim()
            
            if (cleanPassword.length < 6) {
                _authEvent.emit(AuthResult.Error("Password must be at least 6 characters"))
                return@launch
            }
            _isLoading.value = true
            val success = repository.register(cleanUsername, cleanPassword)
            _isLoading.value = false
            if (success) _authEvent.emit(AuthResult.Success)
            else _authEvent.emit(AuthResult.Error("Username already exists"))
        }
    }

    sealed class AuthResult {
        object Success : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
