package com.example.ntpdemo.models.repo

import com.example.ntpdemo.models.ntp.NtpTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeRepository @Inject constructor(
    private val ntpTime: NtpTime
) {
    suspend fun syncTimeWithNtpServer(ntpServerAddress: String) = ntpTime.syncTimeWithNtpServer(ntpServerAddress)

    fun closeSocket() = ntpTime.close()
}