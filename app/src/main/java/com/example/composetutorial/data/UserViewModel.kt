package com.example.composetutorial.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel (application: Application) : AndroidViewModel(application) {
    private val userDao: UserDao = AppDatabase.getInstance(application).userDao()
    private val _isReady = MutableStateFlow(false)

    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _isReady.value = true
        }
    }

    val userData: Flow<User?> = userDao.getUser()

    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        userDao.insert(user)
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.update(user)
        }
    }

}

class UserViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
