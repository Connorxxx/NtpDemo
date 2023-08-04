package com.example.ntpdemo.models.ntp

import com.example.ntpdemo.utils.logCat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NtpServer @Inject constructor() {

    private val socket = DatagramSocket(1234)
    private val receiveData = ByteArray(48)

    suspend fun startNtpServer() {
        withContext(Dispatchers.IO) {
            while (true) {
                receiveData[0] = (0 or (3 shl 3)).toByte()
                val receivePacket = DatagramPacket(receiveData, receiveData.size)
                socket.receive(receivePacket)
                val message = String(receivePacket.data, 0, receivePacket.length)
                "接收到客户端发送的数据：${receivePacket.address.hostAddress}".logCat()
                val responsePacket =
                    DatagramPacket(receiveData, receiveData.size, receivePacket.address, receivePacket.port)
                writeTimeStamp(receiveData, 32, System.currentTimeMillis())
                writeTimeStamp(responsePacket.data, 32, System.currentTimeMillis())
                socket.send(receivePacket)
            }
        }
    }

    private fun writeTimeStamp(buffer: ByteArray, offset: Int, time: Long) {

        var offset = offset
        var seconds = time / 1000L
        val milliseconds = time - seconds * 1000L
        seconds += OFFSET_1900_TO_1970

        // 按大端模式写入秒数
        buffer[offset++] = (seconds shr 24).toByte()
        buffer[offset++] = (seconds shr 16).toByte()
        buffer[offset++] = (seconds shr 8).toByte()
        buffer[offset++] = (seconds shr 0).toByte()
        val fraction = milliseconds * 0x100000000L / 1000L
        // 按大端模式写入小数
        buffer[offset++] = (fraction shr 24).toByte()
        buffer[offset++] = (fraction shr 16).toByte()
        buffer[offset++] = (fraction shr 8).toByte()
        // 低位字节随机
        buffer[offset++] = (Math.random() * 255.0).toInt().toByte()
    }

    companion object {
        private const val OFFSET_1900_TO_1970 = (365L * 70L + 17L) * 24L * 60L * 60L
    }
}