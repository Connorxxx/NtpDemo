package com.example.ntpdemo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ntpdemo.models.repo.TimeRepository
import com.example.ntpdemo.utils.host
import com.example.ntpdemo.utils.logCat
import com.example.ntpdemo.utils.port
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val timeRepository: TimeRepository
) : ViewModel() {


    init {
        viewModelScope.launch {
            launch {
                timeRepository.startNtpServer()
            }
            launch {
                timeRepository.receive().collect {
                    "viewModel receive -> $it".logCat()
                    syncTimeWithNtpServer(it)
                }
            }
        }
    }

    var time by mutableStateOf("")
        private set

    val options = listOf(
        "time.windows.com",
        "time1.google.com",
        "time.apple.com",
        "ntp1.aliyun.com",
        "time.cloudflare.com",
        "192.168.3.46",
        "localhost:1234",
        "192.168.0.165:1234",
        "192.168.0.113:1234",
    )

    /**
     * time & video start time-->
     *
     */
    var selectedOption by mutableStateOf(options[0])

    fun syncTimeWithNtpServer(host: String) {
        viewModelScope.launch {
            timeRepository.syncTimeWithNtpServer(host, 1234)
                .onSuccess {
                    time = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss:SSS",
                        Locale.getDefault()
                    ).format(Date(it))
                }.onFailure {
                time = "Error: ${it.localizedMessage}"
            }
        }
    }

    fun sendBroadcast() {
        viewModelScope.launch {
            timeRepository.sendBroadcast(getIpAddress().toByteArray())
        }
    }

    fun closeSocket() = timeRepository.closeSocket()

    fun getIpAddress() =
        NetworkInterface.getNetworkInterfaces().iterator().asSequence().flatMap {
                it.inetAddresses.asSequence().filter { inetAddress ->
                    inetAddress.isSiteLocalAddress && !inetAddress.hostAddress!!.contains(":") &&
                            inetAddress.hostAddress != "127.0.0.1"
                }.map { inetAddress -> inetAddress.hostAddress }
            }.firstOrNull() ?: ""


    override fun onCleared() {
        super.onCleared()
        timeRepository.closeSocket()
    }
}