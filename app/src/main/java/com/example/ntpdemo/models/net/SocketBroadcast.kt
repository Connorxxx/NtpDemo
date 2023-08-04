package com.example.ntpdemo.models.net

import com.example.ntpdemo.utils.logCat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketBroadcast @Inject constructor() {
    private val socket = DatagramSocket(6464)
    private val receiveData = ByteArray(1024)
    private val address = "255.255.255.255"
    private val receivePacket = DatagramPacket(receiveData, receiveData.size)

    fun receive() = flow {
        while (true) {
            socket.receive(receivePacket)
            val message = String(receivePacket.data, 0, receivePacket.length)
            receivePacket.address.hostAddress?.logCat()
            emit(message)
            receiveData.fill(0)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun send(sendData: ByteArray) {
        withContext(Dispatchers.IO) {
            val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName(address), 6464)
            socket.send(sendPacket)
        }
    }
}