package com.example.composetutorial.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.media.MediaFile
import com.example.composetutorial.media.MediaReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel (application: Application, private val mediaReader: MediaReader) :
    AndroidViewModel(application) {
    private val userDao: UserDao = AppDatabase.getInstance(application).userDao()
    //Splash screen
    private val _isReady = MutableStateFlow(false)

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    var files by mutableStateOf(listOf<MediaFile>())
        private set

    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _isReady.value = true
        }
        viewModelScope.launch (Dispatchers.IO){
            files = mediaReader.getAllMediaFiles()
        }
    }
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(permission: String, isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    //user data
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

class UserViewModelFactory(
    private val application: Application,
    private val mediaReader: MediaReader
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(application, mediaReader) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
