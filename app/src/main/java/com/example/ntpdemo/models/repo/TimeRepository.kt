package com.example.ntpdemo.models.repo

import com.example.ntpdemo.models.net.SocketBroadcast
import com.example.ntpdemo.models.ntp.NtpClient
import com.example.ntpdemo.models.ntp.NtpServer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeRepository @Inject constructor(
    private val ntpTime: NtpClient,
    private val ntpServer: NtpServer,
    private val socketBroadcast: SocketBroadcast
) {
    suspend fun syncTimeWithNtpServer(ntpServerAddress: String, port: Int = 123) = ntpTime.syncTimeWithNtpServer(ntpServerAddress, port)
    fun closeSocket() = ntpTime.close()

    suspend fun startNtpServer() = ntpServer.startNtpServer()

  //  suspend fun startSocketBroadcast() = socketBroadcast.start()

    fun receive() = socketBroadcast.receive()

    suspend fun sendBroadcast(sendData: ByteArray) = socketBroadcast.send(sendData)
}