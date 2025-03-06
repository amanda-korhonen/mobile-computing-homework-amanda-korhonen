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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel (application: Application, private val mediaReader: MediaReader) :
    AndroidViewModel(application) {
    private val userDao: UserDao = AppDatabase.getInstance(application).userDao()
    private val _isReady = MutableStateFlow(false)
    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    var files by mutableStateOf(listOf<MediaFile>())
        private set
    val isReady = _isReady.asStateFlow()
    private val permissionDeniedCountMap = mutableMapOf<String, Int>()
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        viewModelScope.launch {
            delay(2000)
            _isReady.value = true
        }
        viewModelScope.launch (Dispatchers.IO){
            files = mediaReader.getAllMediaFiles()
        }
    }
    //Permission things :)
    fun onPermissionResult(permission: String, isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
            permissionDeniedCountMap[permission] = (permissionDeniedCountMap[permission] ?: 0) + 1
            println("Permission $permission denied. Count: ${permissionDeniedCountMap[permission]}") // Debug Log
        }
    }

    //Doesn't work as intended because shows only the first message all the times.
    fun getPermissionDeniedMessage(): String {
        val deniedPermissions = permissionDeniedCountMap.filter { it.value > 0 }
        if (deniedPermissions.isEmpty()) return ""

        val messages = deniedPermissions.map { (permission, count) ->
            when (count) {
                1 -> "$permission denied. Please allow it to proceed either here or in Settings."
                2 -> "$permission denied twice. Please allow it to continue using the app."
                else -> "$permission denied multiple times. You need to set it in app settings."
            }
        }
        return messages.joinToString("\n")
    }

    fun showSnackbarMessage(message: String?) {
        viewModelScope.launch {
            //println("Snackbar message set: $message") // Debug Log
            _snackbarMessage.emit(message)
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
