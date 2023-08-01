package com.example.ntpdemo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ntpdemo.models.repo.TimeRepository
import com.example.ntpdemo.utils.logCat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val timeRepository: TimeRepository
) : ViewModel() {

    var time by mutableStateOf("")
        private set

    fun syncTimeWithNtpServer() {
        viewModelScope.launch {
            timeRepository.syncTimeWithNtpServer("time.windows.com").onSuccess {
                time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(it))
            }.onFailure {
                time = "Error: ${it.localizedMessage}"
            }
        }
    }

    fun closeSocket() = timeRepository.closeSocket()
}